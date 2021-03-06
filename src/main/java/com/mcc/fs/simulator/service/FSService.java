package com.mcc.fs.simulator.service;

import com.mcc.fs.simulator.config.Constants;
import com.mcc.fs.simulator.exception.ReadDiskFileException;
import com.mcc.fs.simulator.model.filesystem.*;
import com.mcc.fs.simulator.model.helper.DiskHelper;
import com.mcc.fs.simulator.model.users.User;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Slf4j
@Service
public class FSService {

    private final BootBlock bootBlock = new BootBlock();
    private final SuperBlock superBlock = new SuperBlock(); // this one contains the LBL and LIL
    private final InodeList inodeList = new InodeList();

    private final UsersService usersService;
    private final DiskHelper diskHelper;
    private DirectoryBlock RootDirectoryBlock;

    public FSService(UsersService usersService, DiskHelper diskHelper) {
        this.usersService = usersService;
        this.diskHelper = diskHelper;
        bootBlock.init();
        superBlock.init();
        inodeList.init(usersService.getUserByUsername(Constants.DEFAULT_OWNER));
        initRootDirectory();

        // This will happen only at startup
        writeDiskFile();
    }

    private void initRootDirectory() {
        log.info("Initializing root directory ...");

        int[] tableOfContents = new int[11];
        tableOfContents[0] = 8; // Root directory starts in 8 and 9 is the first free block

        User rootUser = usersService.getUserByUsername(Constants.DEFAULT_OWNER);

        DirectoryEntry currentDirectoryEntry = DirectoryEntry.builder().inode((short) 2).name(".").build();
        DirectoryEntry parentDirectoryEntry = DirectoryEntry.builder().inode((short) 2).name("..").build();

        RootDirectoryBlock = new DirectoryBlock();
        RootDirectoryBlock.addEntry(currentDirectoryEntry);
        RootDirectoryBlock.addEntry(parentDirectoryEntry);

        RootDirectoryBlock.setContent(diskHelper.directoryBlockToByteArray(RootDirectoryBlock));

        Inode rootDirectoryInode = Inode.builder() //
                .size(RootDirectoryBlock.getSize()) //
                .type(FileType.DIRECTORY) //
                .owner(rootUser) //
                .creationDate(new Date()) //
                .permissions(FilePermission.OTHERS_CAN_READ_WRITE) //
                .tableOfContents(tableOfContents).build();

        inodeList.registerInode(rootDirectoryInode, Constants.ROOT_DIRECTORY_INODE);
    }

    public String generateDirectoryEntryLine(DirectoryEntry directoryEntry) {
        StringBuilder directoryEntryLineSb = new StringBuilder();

        Inode entryInode = inodeList.getInodeByPosition(directoryEntry.getInode());

        directoryEntryLineSb //
                .append(entryInode.getType().toString()).append(" ") //
                .append(entryInode.getPermissions().toString()).append(" ") //
                .append(directoryEntry.getInode()).append(" ") //
                .append(entryInode.getOwner().getUsername()).append(" ") //
                .append(entryInode.getSize()).append(" ") //
                .append(entryInode.getCreationDate()).append(" ") //
                .append(directoryEntry.getName()).append("<br/>");

        return directoryEntryLineSb.toString();
    }

    private Block readContentBlockByInodeNumber(int inodeNumber) {
        Inode inode = inodeList.getInodeByPosition(inodeNumber);
        byte[] bytes = readBlockBytesByInode(inode);

        return diskHelper.byteArrayToBlock(bytes);
    }

    private DirectoryBlock readDirectoryBlockByInodeNumber(int inodeNumber) {
        try {
            Inode inode = inodeList.getInodeByPosition(inodeNumber);
            byte[] bytes = readBlockBytesByInode(inode);

            return diskHelper.byteArrayToDirectoryBlock(bytes, inode.getSize());
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            throw new ReadDiskFileException();
        }
    }

    private byte[] readBlockBytesByInode(Inode inode) {
        int contentBlock = inode.getTableOfContents()[0];

        int offset = contentBlock * Block.BYTES;

        try {
            byte[] diskFilebytes = FileUtils.readFileToByteArray(new File(Constants.DISK_FILE_PATH));
            byte[] blockBytes = new byte[Block.BYTES];

            System.arraycopy(diskFilebytes, offset, blockBytes, 0, blockBytes.length);

            return blockBytes;
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            throw new ReadDiskFileException();
        }
    }

    public String goToDir(String directoryName, User user) {
        String output;

        DirectoryBlock currentDirectoryBlock = readDirectoryBlockByInodeNumber(user.getCurrentDirectoryInodeNumber());

        if (null != directoryName && !directoryName.equals("")) {

            DirectoryEntry targetDirectoryEntry = currentDirectoryBlock.getEntries().stream()
                    .filter(entry -> entry.getName().equals(directoryName)).findAny().orElse(null);

            if (null != targetDirectoryEntry) {
                user.setCurrentDirectoryInodeNumber(targetDirectoryEntry.getInode());
                usersService.updateUser(user);

                return "Moved to directory " + targetDirectoryEntry.getName();
            } else {
                return "Directory " + directoryName + " not found.";
            }

        } else {
            output = "specify a valid directory";
        }

        return output;
    }

    public String listDir(String directoryName, User user) {
        DirectoryBlock directoryBlockToList;

        DirectoryBlock currentDirectoryBlock = readDirectoryBlockByInodeNumber(user.getCurrentDirectoryInodeNumber());

        if (null != directoryName && !directoryName.equals("")) {

            DirectoryEntry targetDirectoryEntry = currentDirectoryBlock.getEntries().stream()
                    .filter(entry -> entry.getName().equals(directoryName)).findAny().orElse(null);

            if (null != targetDirectoryEntry) {
                directoryBlockToList = readDirectoryBlockByInodeNumber(targetDirectoryEntry.getInode());
            } else {
                return "Directory " + directoryName + " not found.";
            }

        } else {
            directoryBlockToList = currentDirectoryBlock;
        }

        StringBuilder output = new StringBuilder();

        for (DirectoryEntry entry : directoryBlockToList.getEntries()) {
            Inode entryInode = inodeList.getInodeByPosition(entry.getInode());

            boolean isOwner = entryInode.getOwner().getId() == user.getId();
            boolean othersCanReadPermissions = !entryInode.getPermissions().equals(FilePermission.RESTRICTED_TO_OWNER);

            if (isOwner || othersCanReadPermissions) {
                output //
                        .append(entryInode.getType().toString()).append(" ") //
                        .append(entryInode.getPermissions().toString()).append(" ") //
                        .append(entry.getInode()).append(" ") //
                        .append(entryInode.getOwner().getUsername()).append(" ") //
                        .append(entryInode.getSize()).append(" ") //
                        .append(entryInode.getCreationDate()).append(" ") //
                        .append(entry.getName()).append("<br/>");
            }

        }

        return output.toString();
    }

    public String createDir(String dirname, User user, FilePermission filePermissions) {
        String output;

        int currentDirectoryInodeNumber = user.getCurrentDirectoryInodeNumber();

        byte freeInodeNumber = superBlock.getNextFreeInode();
        byte freeBlockNumber = superBlock.getNextFreeBlock();

        DirectoryBlock newDirectoryBlock = new DirectoryBlock();
        int[] tableOfContents = new int[11];
        tableOfContents[0] = freeBlockNumber; // Root directoy starts in 8 and 9 is the first free block

        DirectoryEntry newCurrentDirectoryEntry = DirectoryEntry.builder().inode(freeInodeNumber).name(".").build();
        DirectoryEntry parentDirectoryEntry = DirectoryEntry.builder().inode((short) currentDirectoryInodeNumber).name("..").build();

        Inode currentDirectoryInode = inodeList.getInodeByPosition(currentDirectoryInodeNumber);
        int contentBlock = currentDirectoryInode.getTableOfContents()[0]; // Bloque de contenido del padre
        long offset = (long) contentBlock * Block.BYTES;

        RandomAccessFile diskFile = null;

        try {
            byte[] diskFilebytes = FileUtils.readFileToByteArray(new File(Constants.DISK_FILE_PATH));
            byte[] directoryBlockBytes = new byte[Block.BYTES];

            System.arraycopy(diskFilebytes, (int) offset, directoryBlockBytes, 0, directoryBlockBytes.length);
            DirectoryBlock currentDirectoryBlock = diskHelper.byteArrayToDirectoryBlock(directoryBlockBytes, currentDirectoryInode.getSize());
            DirectoryEntry newDirectoryEntry = DirectoryEntry.builder().inode(freeInodeNumber).name(dirname).build();
            currentDirectoryBlock.addEntry(newDirectoryEntry); // Se agrega dir al current

            newDirectoryBlock.addEntry(newCurrentDirectoryEntry);
            newDirectoryBlock.addEntry(parentDirectoryEntry);

            newDirectoryBlock.setContent(diskHelper.directoryBlockToByteArray(newDirectoryBlock));
            currentDirectoryBlock.setContent(diskHelper.directoryBlockToByteArray(currentDirectoryBlock));

            Inode newDirectoryInode = Inode.builder() //
                    .size(newDirectoryBlock.getSize()) //
                    .type(FileType.DIRECTORY) //
                    .owner(user) //
                    .creationDate(new Date()) //
                    .permissions(filePermissions) //
                    .tableOfContents(tableOfContents).build();

            inodeList.registerInode(newDirectoryInode, freeInodeNumber);

            currentDirectoryInode.setSize(currentDirectoryBlock.getSize());
            inodeList.registerInode(currentDirectoryInode, currentDirectoryInodeNumber);

            log.info("Opening disk file in {} mode", FileAccessMode.READ_WRITE);
            diskFile = new RandomAccessFile(Constants.DISK_FILE_PATH, FileAccessMode.READ_WRITE.toString());

            // writing parent directory
            log.info("writing inode parent directory into disk file with fd={}", diskFile.getFD().toString());
            offset = (long) currentDirectoryInode.getTableOfContents()[0] * Block.BYTES;
            log.info("parent directory block={}, offset={}", currentDirectoryBlock, offset);
            diskFile.seek(offset);
            diskFile.write(currentDirectoryBlock.getContent());
            log.info("root directory content created");

            // writing new directory
            log.info("writing new directory into disk file with fd={}", diskFile.getFD().toString());
            offset = (long) newDirectoryInode.getTableOfContents()[0] * Block.BYTES;
            log.info("new directory block={}, offset={}", currentDirectoryBlock, offset);
            diskFile.seek(offset);
            diskFile.write(newDirectoryBlock.getContent());
            log.info("new directory content created");

            output = "Directory Created";
        } catch (IOException e) {
            log.error("Unable to create disk file. Cause: {}", e.getMessage(), e);
            output = "Unable to create directory";
            System.exit(1);
        } finally {
            if (diskFile != null) {
                try {
                    log.info("Closing disk file with fd={}", diskFile.getFD().toString());
                    diskFile.close();
                } catch (IOException e) {
                    log.error("Unable to close diskfile", e);
                }
            }
        }

        log.info("LBL peek {}:", superBlock.getLBLqueue().peek());
        log.info("LIL peek {}:", superBlock.getLILqueue().peek());

        return output;
    }

    public String RemoveDir() {
        String removed = ".<br/>..";
        return removed;
    }

    public String MoveDir() {
        String moveto = ".<br/>..";
        return moveto;
    }

    public String showFile(String filename, User user) {
        DirectoryBlock currentDirectoryBlock = readDirectoryBlockByInodeNumber(user.getCurrentDirectoryInodeNumber());

        DirectoryEntry fileEntry = currentDirectoryBlock.getEntries().stream()
                .filter(entry -> entry.getName().equals(filename)).findAny().orElse(null);

        Block fileBlock;
        Inode fileInode;

        if (null != fileEntry) {
            fileInode = inodeList.getInodeByPosition(fileEntry.getInode());
            fileBlock = readContentBlockByInodeNumber(fileEntry.getInode());
        } else {
            return "File " + filename + " not found.";
        }

        return diskHelper.blockContentToString(fileBlock, fileInode.getSize());
    }

    public String createFile(String filename, String content, User user, FilePermission filePermissions) {
        String output;

        int currentDirectoryInodeNumber = user.getCurrentDirectoryInodeNumber();

        byte freeInodeNumber = superBlock.getNextFreeInode();
        byte freeBlockNumber = superBlock.getNextFreeBlock();

        Inode currentDirectoryInode = inodeList.getInodeByPosition(currentDirectoryInodeNumber);
        int contentBlock = currentDirectoryInode.getTableOfContents()[0]; // Bloque de contenido del padre
        long offset = (long) contentBlock * Block.BYTES;

        RandomAccessFile diskFile = null;

        try {
            byte[] diskFilebytes = FileUtils.readFileToByteArray(new File(Constants.DISK_FILE_PATH));
            byte[] directoryBlockBytes = new byte[Block.BYTES];

            System.arraycopy(diskFilebytes, (int) offset, directoryBlockBytes, 0, directoryBlockBytes.length);
            DirectoryBlock currentDirectoryBlock = diskHelper.byteArrayToDirectoryBlock(directoryBlockBytes, currentDirectoryInode.getSize());

            DirectoryEntry newFileEntry = DirectoryEntry.builder().inode(freeInodeNumber).name(filename).build();
            currentDirectoryBlock.addEntry(newFileEntry);

            Block newFileBlock = diskHelper.contentToBlock(content);
            currentDirectoryBlock.setContent(diskHelper.directoryBlockToByteArray(currentDirectoryBlock));

            int[] tableOfContents = new int[11];
            tableOfContents[0] = freeBlockNumber;

            Inode newFileInode = Inode.builder() //
                    .size(content.getBytes(StandardCharsets.UTF_8).length) //
                    .type(FileType.REGULAR_FILE) //
                    .owner(user) //
                    .creationDate(new Date()) //
                    .permissions(filePermissions) //
                    .tableOfContents(tableOfContents).build();

            inodeList.registerInode(newFileInode, freeInodeNumber);

            currentDirectoryInode.setSize(currentDirectoryBlock.getSize());
            inodeList.registerInode(currentDirectoryInode, currentDirectoryInodeNumber);

            log.info("Opening disk file in {} mode", FileAccessMode.READ_WRITE);
            diskFile = new RandomAccessFile(Constants.DISK_FILE_PATH, FileAccessMode.READ_WRITE.toString());

            // writing parent directory
            log.info("writing inode parent directory into disk file with fd={}", diskFile.getFD().toString());
            offset = (long) currentDirectoryInode.getTableOfContents()[0] * Block.BYTES;
            log.info("parent directory block={}, offset={}", currentDirectoryBlock, offset);
            diskFile.seek(offset);
            diskFile.write(currentDirectoryBlock.getContent());
            log.info("root directory content created");

            // writing new file
            log.info("writing new file into disk file with fd={}", diskFile.getFD().toString());
            offset = (long) newFileInode.getTableOfContents()[0] * Block.BYTES;
            log.info("new file block={}, offset={}", newFileBlock.getContent(), offset);
            diskFile.seek(offset);
            diskFile.write(newFileBlock.getContent());
            log.info("new file {} created", filename);

            output = "File " + filename + " Created";
        } catch (IOException e) {
            log.error("Unable to create disk file. Cause: {}", e.getMessage(), e);
            output = "Unable to create directory";
            System.exit(1);
        } finally {
            if (diskFile != null) {
                try {
                    log.info("Closing disk file with fd={}", diskFile.getFD().toString());
                    diskFile.close();
                } catch (IOException e) {
                    log.error("Unable to close diskfile", e);
                }
            }
        }

        log.info("LBL peek {}:", superBlock.getLBLqueue().peek());
        log.info("LIL peek {}:", superBlock.getLILqueue().peek());

        return output;
    }

    public String removeFile(String filename, User user) {
        String output;

        // finding the file to remove
        Inode currentDirectoryInode = inodeList.getInodeByPosition(user.getCurrentDirectoryInodeNumber());
        DirectoryBlock currentDirectoryBlock = readDirectoryBlockByInodeNumber(user.getCurrentDirectoryInodeNumber());
        DirectoryEntry fileEntry = currentDirectoryBlock.getEntries().stream()
                .filter(entry -> entry.getName().equals(filename)).findAny().orElse(null);

        if (null == fileEntry) {
            return "file " + filename + " does not exist";
        }

        // reset inode, set back to the queue the block and inodes

        Inode fileInodeToRemove = inodeList.getInodeByPosition(fileEntry.getInode());

        if (fileInodeToRemove.getType().equals(FileType.DIRECTORY)) {
            DirectoryBlock directoryBlock = readDirectoryBlockByInodeNumber(fileEntry.getInode());
            if (directoryBlock.getEntries().size() > 2) {
                return "the directory is not empty";
            }
        }

        boolean isOwner = fileInodeToRemove.getOwner().getId() == user.getId();
        boolean othersCanWrite = fileInodeToRemove.getPermissions().equals(FilePermission.OTHERS_CAN_READ_WRITE);

        if (!isOwner && !othersCanWrite) {
            return "insufficient permissions";
        }

        int blockNumberToRemove = fileInodeToRemove.getTableOfContents()[0];
        int inodeNumberToRemove = fileEntry.getInode();

        fileInodeToRemove.setSize(0);
        fileInodeToRemove.setType(FileType.FREE_INODE);
        fileInodeToRemove.setOwner(usersService.getUserByUsername(Constants.DEFAULT_OWNER));
        fileInodeToRemove.setCreationDate(new Date());
        fileInodeToRemove.setPermissions(FilePermission.OTHERS_CAN_READ);
        fileInodeToRemove.setTableOfContents(new int[11]);

        inodeList.registerInode(fileInodeToRemove, inodeNumberToRemove);

        superBlock.registerFreeInode((byte) inodeNumberToRemove);
        superBlock.registerFreeBlock((byte) blockNumberToRemove);

        currentDirectoryBlock.removeEntry(fileEntry);
        currentDirectoryBlock.setContent(diskHelper.directoryBlockToByteArray(currentDirectoryBlock));
        currentDirectoryInode.setSize(currentDirectoryBlock.getSize());

        inodeList.registerInode(currentDirectoryInode, user.getCurrentDirectoryInodeNumber());

        // writing blocks to disk

        RandomAccessFile diskFile = null;
        long offset;

        try {
            log.info("Opening disk file in {} mode", FileAccessMode.READ_WRITE);
            diskFile = new RandomAccessFile(Constants.DISK_FILE_PATH, FileAccessMode.READ_WRITE.toString());

            // writing current directory
            log.info("writing current directory into disk file with fd={}", diskFile.getFD().toString());
            offset = (long) currentDirectoryInode.getTableOfContents()[0] * Block.BYTES;
            log.info("current directory block={}, offset={}", currentDirectoryBlock, offset);
            diskFile.seek(offset);
            diskFile.write(currentDirectoryBlock.getContent());
            log.info("current directory content written");

            // reseting block of removed file
            Block srcBlock = readContentBlockByInodeNumber(inodeNumberToRemove);
            srcBlock.setContent(new byte[Block.BYTES]);
            log.info("reseting file to remove into disk file with fd={}", diskFile.getFD().toString());
            offset = (long) fileInodeToRemove.getTableOfContents()[0] * Block.BYTES;
            log.info("file to remove block={}, offset={}", srcBlock.getContent(), offset);
            diskFile.seek(offset);
            diskFile.write(srcBlock.getContent());

            output = filename + " removed";
        } catch (IOException e) {
            log.error("Unable to create disk file. Cause: {}", e.getMessage(), e);
            output = "Unable to create directory";
        } finally {
            if (diskFile != null) {
                try {
                    log.info("Closing disk file with fd={}", diskFile.getFD().toString());
                    diskFile.close();
                } catch (IOException e) {
                    log.error("Unable to close diskfile", e);
                }
            }
        }

        log.info("LBL peek {}:", superBlock.getLBLqueue().peek());
        log.info("LIL peek {}:", superBlock.getLILqueue().peek());

        return output;
    }

    public String moveFile(String filename, String targetDirectory, User user) {
        String output;

        // finding the file to move
        DirectoryBlock currentDirectoryBlock = readDirectoryBlockByInodeNumber(user.getCurrentDirectoryInodeNumber());
        DirectoryEntry fileEntry = currentDirectoryBlock.getEntries().stream()
                .filter(entry -> entry.getName().equals(filename)).findAny().orElse(null);

        if (null == fileEntry) {
            return "file " + filename + " does not exist";
        }

        // finding the target directory
        DirectoryEntry targetDirectoryEntry = currentDirectoryBlock.getEntries().stream()
                .filter(entry -> entry.getName().equals(targetDirectory)).findAny().orElse(null);

        if (null == targetDirectoryEntry) {
            return "target directory not found";
        }

        Inode targetDirectoryInode = inodeList.getInodeByPosition(targetDirectoryEntry.getInode());

        if (targetDirectoryInode.getType() != FileType.DIRECTORY) {
            return "target must be a directory";
        }

        DirectoryBlock targetDirectoryBlock = readDirectoryBlockByInodeNumber(targetDirectoryEntry.getInode());

        // remove the entry from current directory and add it to the target directory block
        currentDirectoryBlock.removeEntry(fileEntry);
        currentDirectoryBlock.setContent(diskHelper.directoryBlockToByteArray(currentDirectoryBlock));

        // add the entry to target directory, if target directory does not exist return an error message
        targetDirectoryBlock.addEntry(fileEntry);
        targetDirectoryBlock.setContent(diskHelper.directoryBlockToByteArray(targetDirectoryBlock));

        // update inodes
        Inode currentDirectoryInode = inodeList.getInodeByPosition(user.getCurrentDirectoryInodeNumber());
        targetDirectoryInode.setSize(targetDirectoryBlock.getSize());
        currentDirectoryInode.setSize(currentDirectoryBlock.getSize());

        inodeList.registerInode(targetDirectoryInode, targetDirectoryEntry.getInode());
        inodeList.registerInode(currentDirectoryInode, user.getCurrentDirectoryInodeNumber());

        // writing blocks to disk

        RandomAccessFile diskFile = null;
        long offset;

        try {
            log.info("Opening disk file in {} mode", FileAccessMode.READ_WRITE);
            diskFile = new RandomAccessFile(Constants.DISK_FILE_PATH, FileAccessMode.READ_WRITE.toString());

            // writing current directory
            log.info("writing current directory into disk file with fd={}", diskFile.getFD().toString());
            offset = (long) currentDirectoryInode.getTableOfContents()[0] * Block.BYTES;
            log.info("current directory block={}, offset={}", currentDirectoryBlock, offset);
            diskFile.seek(offset);
            diskFile.write(currentDirectoryBlock.getContent());
            log.info("current directory content written");

            // writing target directory
            log.info("writing target directory into disk file with fd={}", diskFile.getFD().toString());
            offset = (long) targetDirectoryInode.getTableOfContents()[0] * Block.BYTES;
            log.info("target directory block={}, offset={}", targetDirectoryBlock, offset);
            diskFile.seek(offset);
            diskFile.write(targetDirectoryBlock.getContent());
            log.info("target directory content written");

            // writing new file
            Inode newFileInode = inodeList.getInodeByPosition(fileEntry.getInode());
            Block srcBlock = readContentBlockByInodeNumber(fileEntry.getInode()); // this is the content block to be copied
            log.info("writing new copied into disk file with fd={}", diskFile.getFD().toString());
            offset = (long) newFileInode.getTableOfContents()[0] * Block.BYTES;
            log.info("new file block={}, offset={}", srcBlock.getContent(), offset);
            diskFile.seek(offset);
            diskFile.write(srcBlock.getContent());
            log.info("new file {} copied into {}", filename, targetDirectory);

            output = "File " + filename + " moved to " + targetDirectory;
        } catch (IOException e) {
            log.error("Unable to create disk file. Cause: {}", e.getMessage(), e);
            output = "Unable to create directory";
            System.exit(1);
        } finally {
            if (diskFile != null) {
                try {
                    log.info("Closing disk file with fd={}", diskFile.getFD().toString());
                    diskFile.close();
                } catch (IOException e) {
                    log.error("Unable to close diskfile", e);
                }
            }
        }

        log.info("LBL peek {}:", superBlock.getLBLqueue().peek());
        log.info("LIL peek {}:", superBlock.getLILqueue().peek());

        return output;
    }

    public String copyFile(String filename, String targetDirectory, User user) {
        String output;

        // finding the file to copy
        DirectoryBlock currentDirectoryBlock = readDirectoryBlockByInodeNumber(user.getCurrentDirectoryInodeNumber());
        DirectoryEntry originalFileEntry = currentDirectoryBlock.getEntries().stream()
                .filter(entry -> entry.getName().equals(filename)).findAny().orElse(null);

        if (null == originalFileEntry) {
            return "file " + filename + " does not exist";
        }

        // finding the target directory
        DirectoryEntry targetDirectoryEntry = currentDirectoryBlock.getEntries().stream()
                .filter(entry -> entry.getName().equals(targetDirectory)).findAny().orElse(null);

        if (null == targetDirectoryEntry) {
            return "target directory not found";
        }

        Inode targetDirectoryInode = inodeList.getInodeByPosition(targetDirectoryEntry.getInode());

        if (targetDirectoryInode.getType() != FileType.DIRECTORY) {
            return "target must be a directory";
        }

        DirectoryBlock targetDirectoryBlock = readDirectoryBlockByInodeNumber(targetDirectoryEntry.getInode());

        // create a new Entry since we need a new inode number and free block number for the copy
        byte freeInode = superBlock.getNextFreeInode();
        byte freeBlock = superBlock.getNextFreeBlock();

        Inode originalFileInode = inodeList.getInodeByPosition(originalFileEntry.getInode());

        int[] newFileTableOfContents = new int[11];
        newFileTableOfContents[0] = freeBlock;

        Inode newFileInode = Inode.builder() //
                .size(originalFileInode.getSize()) //
                .type(originalFileInode.getType()) //
                .owner(user) //
                .creationDate(new Date()) //
                .permissions(originalFileInode.getPermissions()) //
                .tableOfContents(newFileTableOfContents).build();

        Block newFileBlock = readContentBlockByInodeNumber(originalFileEntry.getInode()); // this is the content block to be copied

        DirectoryEntry newFileEntry = DirectoryEntry.builder()
                .inode(freeInode)
                .name(originalFileEntry.getName())
                .build();

        // add the entry to target directory, if target directory does not exist return an error message
        targetDirectoryBlock.addEntry(newFileEntry);
        targetDirectoryBlock.setContent(diskHelper.directoryBlockToByteArray(targetDirectoryBlock));

        // update target directory inode
        targetDirectoryInode.setSize(targetDirectoryBlock.getSize());
        inodeList.registerInode(targetDirectoryInode, targetDirectoryEntry.getInode());

        // register new file inode
        inodeList.registerInode(newFileInode, freeInode);

        // writing blocks to disk

        RandomAccessFile diskFile = null;
        long offset;

        try {
            log.info("Opening disk file in {} mode", FileAccessMode.READ_WRITE);
            diskFile = new RandomAccessFile(Constants.DISK_FILE_PATH, FileAccessMode.READ_WRITE.toString());

            // writing new file
            log.info("writing new copied into disk file with fd={}", diskFile.getFD().toString());
            offset = (long) newFileInode.getTableOfContents()[0] * Block.BYTES;
            log.info("new file block={}, offset={}", newFileBlock.getContent(), offset);
            diskFile.seek(offset);
            diskFile.write(newFileBlock.getContent());
            log.info("new file {} copied into {}", filename, targetDirectory);

            // writing target directory block
            log.info("writing target directory into disk file with fd={}", diskFile.getFD().toString());
            offset = (long) targetDirectoryInode.getTableOfContents()[0] * Block.BYTES;
            log.info("target directory block={}, offset={}", targetDirectoryBlock, offset);
            diskFile.seek(offset);
            diskFile.write(targetDirectoryBlock.getContent());
            log.info("target directory content written");

            output = "File " + filename + " copied to " + targetDirectory;
        } catch (IOException e) {
            log.error("Unable to create disk file. Cause: {}", e.getMessage(), e);
            output = "Unable to create directory";
            System.exit(1);
        } finally {
            if (diskFile != null) {
                try {
                    log.info("Closing disk file with fd={}", diskFile.getFD().toString());
                    diskFile.close();
                } catch (IOException e) {
                    log.error("Unable to close diskfile", e);
                }
            }
        }

        log.info("LBL peek {}:", superBlock.getLBLqueue().peek());
        log.info("LIL peek {}:", superBlock.getLILqueue().peek());

        return output;
    }

    public void writeDiskFile() {
        RandomAccessFile diskFile = null;

        try {
            long offset = 0;
            log.info("Opening disk file in {} mode", FileAccessMode.READ_WRITE);
            diskFile = new RandomAccessFile(Constants.DISK_FILE_PATH, FileAccessMode.READ_WRITE.toString());

            // writing boot
            log.info("Writing boot into disk file with fd={}", diskFile.getFD().toString());
            diskFile.write(bootBlock.getContent());
            offset += BootBlock.BYTES;
            diskFile.seek(offset);
            log.info("Setting file seek to={}", offset);

            // writing LIL
            log.info("Writing LIL into disk file with fd={}", diskFile.getFD().toString());
            diskFile.write(superBlock.getLIL());
            offset += superBlock.getLIL().length;
            diskFile.seek(offset);
            log.info("Setting file seek to={}", offset);

            // writing LBL
            log.info("Writing LBL into disk file with fd={}", diskFile.getFD().toString());
            diskFile.write(superBlock.getLBL());
            offset += superBlock.getLBL().length;
            diskFile.seek(offset);
            log.info("Setting file seek to={}", offset);

            // writing Inode List
            log.info("Writing Inode List into disk file with fd={}", diskFile.getFD().toString());
            for (Inode inode : inodeList.getInodes()) {
                byte[] inodeBytes = diskHelper.inodeToByteArray(inode);
                diskFile.write(inodeBytes);
                offset += Inode.BYTES;
                diskFile.seek(offset);
                log.info("Setting file seek to={}", offset);
            }
            log.info("Finished writing Inode List");

            // writing data blocks
            log.info("Writing empty data blocks into disk file with fd={}", diskFile.getFD().toString());
            int totalFreeDataBlocks = 1017; // 7 blocks of 1K are already in use
            for (int i = 0; i < totalFreeDataBlocks; i++) {
                Block emptyBlock = new Block();
                emptyBlock.initEmpty();

                diskFile.write(emptyBlock.getContent());
                offset += Block.BYTES;
                diskFile.seek(offset);
                log.info("Setting file seek to={}", offset);
            }
            log.info("wrote {} blocks of 1K", totalFreeDataBlocks);

            // writing root directory
            log.info("Writing inode root directory into disk file with fd={}", diskFile.getFD().toString());
            Inode directoryInode = inodeList.getInodeByPosition(Constants.ROOT_DIRECTORY_INODE);
            int rootDirectoryBlock = directoryInode.getTableOfContents()[0];
            offset = (long) rootDirectoryBlock * Block.BYTES;
            log.info("Root directory block={}, offset={}", rootDirectoryBlock, offset);
            diskFile.seek(offset);
            diskFile.write(RootDirectoryBlock.getContent());
            log.info("root directory content created");

        } catch (FileNotFoundException e) {
            log.error("Unable to create disk file. Cause: {}", e.getMessage(), e);
            System.exit(1);
        } catch (IOException e) {
            log.error("Unable to write to disk file. Cause: {}", e.getMessage(), e);
            System.exit(1);
        } finally {
            if (diskFile != null) {
                try {
                    log.info("Closing disk file with fd={}", diskFile.getFD().toString());
                    diskFile.close();
                } catch (IOException e) {
                    log.error("Unable to close diskfile", e);
                }
            }
        }

    }

    public void readDiskFile() {
        RandomAccessFile diskFile = null;

        try {
            log.info("Opening disk file in {} mode", FileAccessMode.READ);
            diskFile = new RandomAccessFile(Constants.DISK_FILE_PATH, FileAccessMode.READ.toString());
            byte[] content = new byte[(int) diskFile.length()];
            log.info("Reading disk file");
            diskFile.read(content);
            int counter = 0;

            for (byte b : content) {
                if (counter == 1024) {
                    System.out.print("\n");
                    counter = 0;
                }
                System.out.print(b);
                counter++;
            }
        } catch (IOException e) {
            log.error("Unable to read disk file. Cause: {}", e.getMessage(), e);
            System.exit(1);
        } finally {
            if (diskFile != null) {
                try {
                    log.info("Closing disk file with fd={}", diskFile.getFD().toString());
                    diskFile.close();
                } catch (IOException e) {
                    log.error("Unable to close diskfile", e);
                }
            }
        }
    }

}
