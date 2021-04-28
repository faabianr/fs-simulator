package com.mcc.fs.simulator.service;

import com.mcc.fs.simulator.config.Constants;
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
    private final int currentDirectoryInodeNumber = Constants.ROOT_DIRECTORY_INODE;

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
        tableOfContents[0] = 8; // Root directoy starts in 8 and 9 is the first free block

        User rootUser = usersService.getUserByUsername(Constants.DEFAULT_OWNER);

        DirectoryEntry parentDirectoryEntry = DirectoryEntry.builder().inode((short) 2).name("..").build();
        DirectoryEntry currentDirectoryEntry = DirectoryEntry.builder().inode((short) 2).name(".").build();

        RootDirectoryBlock = new DirectoryBlock();
        RootDirectoryBlock.addEntry(parentDirectoryEntry);
        RootDirectoryBlock.addEntry(currentDirectoryEntry);

        RootDirectoryBlock.setContent(diskHelper.directoryBlockToByteArray(RootDirectoryBlock));

        Inode rootDirectoryInode = Inode.builder() //
                .size(RootDirectoryBlock.getSize()) //
                .type(FileType.DIRECTORY) //
                .owner(rootUser) //
                .creationDate(new Date()) //
                .permissions(Constants.DEFAULT_PERMISSIONS) //
                .tableOfContents(tableOfContents).build();

        inodeList.registerInode(rootDirectoryInode, Constants.ROOT_DIRECTORY_INODE);
    }

    public String listdir() {
        StringBuilder output = new StringBuilder();
        Inode currentDirectoryInode = inodeList.getInodeByPosition(currentDirectoryInodeNumber);
        int contentBlock = currentDirectoryInode.getTableOfContents()[0];
        int offset = contentBlock * Block.BYTES;
        try {
            byte[] diskFilebytes = FileUtils.readFileToByteArray(new File(Constants.DISK_FILE_PATH));
            byte[] directoryBlockBytes = new byte[Block.BYTES];

            System.arraycopy(diskFilebytes, offset, directoryBlockBytes, 0, directoryBlockBytes.length);
            DirectoryBlock currentDirectoryBlock = diskHelper.byteArrayToDirectoryBlock(
                    directoryBlockBytes, currentDirectoryInode.getSize());
            for (DirectoryEntry entry : currentDirectoryBlock.getEntries()) {
                output.append(entry.getName()).append("<br/>");
            }
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            output = new StringBuilder("unable to list directory");
        }
        return output.toString();
    }

    public String createDir(String dirname) {
        String output;

        byte freeInodeNumber = superBlock.getNextFreeInode();
        byte freeBlockNumber = superBlock.getNextFreeBlock();

        DirectoryBlock newDirectoryBlock = new DirectoryBlock();
        int[] tableOfContents = new int[11];
        tableOfContents[0] = freeBlockNumber; // Root directoy starts in 8 and 9 is the first free block
        User rootUser = usersService.getUserByUsername(Constants.DEFAULT_OWNER);

        DirectoryEntry parentDirectoryEntry = DirectoryEntry.builder().inode((short) currentDirectoryInodeNumber).name("..").build();
        DirectoryEntry newCurrentDirectoryEntry = DirectoryEntry.builder().inode(freeInodeNumber).name(".").build();

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

            newDirectoryBlock.addEntry(parentDirectoryEntry);
            newDirectoryBlock.addEntry(newCurrentDirectoryEntry);

            newDirectoryBlock.setContent(diskHelper.directoryBlockToByteArray(newDirectoryBlock));
            currentDirectoryBlock.setContent(diskHelper.directoryBlockToByteArray(currentDirectoryBlock));

            Inode newDirectoryInode = Inode.builder() //
                    .size(newDirectoryBlock.getSize()) //
                    .type(FileType.DIRECTORY) //
                    .owner(rootUser) //
                    .creationDate(new Date()) //
                    .permissions(Constants.DEFAULT_PERMISSIONS) //
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

    public String CreateFile() {
        String craetef = ".<br/>..";
        return craetef;
    }

    public String RemoveFile() {
        String revomed = ".<br/>..";
        return revomed;
    }

    public String MoveFile() {
        String movef = ".<br/>..";
        return movef;
    }

    public String CopyFile() {
        String copyf = ".<br/>..";
        return copyf;
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
