package com.mcc.fs.simulator.service;

import com.mcc.fs.simulator.config.Defaults;
import com.mcc.fs.simulator.model.filesystem.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Date;

@Slf4j
@Service
public class FSService {

    /*
     *
     *  blocks:
     *      1: boot block
     *      2 - 3: super block (LIL, LBL)
     *      4 - 7: inodes list
     *      8: not used
     *      9: root directory
     *      10 - n : initial free data blocks
     *
     * */

    private static final String DISK_FILE_PATH = "./diskfile";
    private final BootBlock bootBlock = new BootBlock();
    private final SuperBlock superBlock = new SuperBlock(); // this one contains the LBL and LIL
    private final InodeList inodeList = new InodeList();
    private DirectoryBlock rootDirectory;

    public FSService() {
        bootBlock.init();
        superBlock.init();
        inodeList.init();
        initRootDirectory();
    }


    private void initRootDirectory() {
        log.info("Initializing root directory ...");

        int[] tableOfContents = new int[11];
        tableOfContents[0] = 9;

        Inode rootDirectoryInode = Inode.builder()
                .size(1024).type(FileType.DIRECTORY).owner(Defaults.OWNER).creationDate(new Date()).permissions(Defaults.PERMISSIONS).tableOfContents(tableOfContents).build();

        DirectoryEntry parentDirectoryEntry = DirectoryEntry.builder().inode((byte) 2).name("..").build();
        DirectoryEntry currentDirectoryEntry = DirectoryEntry.builder().inode((byte) 2).name(".").build();

        DirectoryBlock rootDirectory = new DirectoryBlock();
        rootDirectory.addEntry(parentDirectoryEntry);
        rootDirectory.addEntry(parentDirectoryEntry);

        rootDirectory.writeToDisk();
    }

    public void writeDiskFile() {
        RandomAccessFile diskFile = null;

        try {
            long offset = 0;
            log.info("Opening disk file in {} mode", FileAccessMode.READ_WRITE);
            diskFile = new RandomAccessFile(DISK_FILE_PATH, FileAccessMode.READ_WRITE.toString());

            // writing boot
            log.info("Writing boot into disk file with fd={}", diskFile.getFD().toString());
            diskFile.write(bootBlock.getContent());
            offset += BootBlock.SIZE;
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

            // writing Inode Table
            /*
            log.info("Writing Inode Table into disk file with fd={}", diskFile.getFD().toString());
            diskFile.write(InodeTable);
            offset += InodeTable.length;
            diskFile.seek(offset);
            log.info("Setting file seek to={}", offset);

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
            diskFile = new RandomAccessFile(DISK_FILE_PATH, FileAccessMode.READ.toString());
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
