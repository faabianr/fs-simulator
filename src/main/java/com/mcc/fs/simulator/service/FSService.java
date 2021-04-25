package com.mcc.fs.simulator.service;

import com.mcc.fs.simulator.config.Constants;
import com.mcc.fs.simulator.model.filesystem.*;
import com.mcc.fs.simulator.model.helper.DiskHelper;
import com.mcc.fs.simulator.model.users.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

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
        tableOfContents[0] = 9;

        User rootUser = usersService.getUserByUsername(Constants.DEFAULT_OWNER);

        Inode rootDirectoryInode = Inode.builder()
                .size(DirectoryBlock.BYTES).type(FileType.DIRECTORY).owner(rootUser).creationDate(new Date()).permissions(Constants.DEFAULT_PERMISSIONS).tableOfContents(tableOfContents).build();

        DirectoryEntry parentDirectoryEntry = DirectoryEntry.builder().inode((byte) 2).name("..").build();
        DirectoryEntry currentDirectoryEntry = DirectoryEntry.builder().inode((byte) 2).name(".").build();

        DirectoryBlock rootDirectory = new DirectoryBlock();
        rootDirectory.addEntry(parentDirectoryEntry);
        rootDirectory.addEntry(currentDirectoryEntry);

        inodeList.registerInode(rootDirectoryInode, Constants.ROOT_DIRECTORY_INODE);
    }

    public String listdir() {
        String list = ".<br/>..";
        return list;
    }

    public String CreateDir() {
        String created = ".<br/>..";
        return created;
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

            /*
            // writing root directory
            log.info("Writing inode root directory into disk file with fd={}", diskFile.getFD().toString());
            offset = (LBL.length * 3L) + 1; // es +1 porque el primer inodo no se usa
            log.info("offset={}", offset);
            diskFile.seek(offset);
            diskFile.write(rootDirectory.getInode());
            log.info("Setting file seek to={}", offset);

            log.info("Writing content root directory into disk file with fd={}", diskFile.getFD().toString());
            offset += InodeTable.length - 2; // Le quitamos el 1 que contamos en el offset del inode root
            log.info("offset={}", offset);
            diskFile.seek(offset);
            diskFile.writeChar('.');
            diskFile.writeChars("..");
            log.info("Setting file seek to={}", offset);
            */

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
