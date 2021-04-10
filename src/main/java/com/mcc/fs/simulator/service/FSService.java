package com.mcc.fs.simulator.service;

import com.mcc.fs.simulator.model.filesystem.Directory;
import com.mcc.fs.simulator.model.filesystem.FileAccessMode;
import com.mcc.fs.simulator.model.filesystem.FileType;
import com.mcc.fs.simulator.model.filesystem.Inode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Arrays;
import java.util.Date;

@Slf4j
@Service
public class FSService {

    private static final String DISK_FILE_PATH = "./diskfile";

    public static final String DEFAULT_OWNER = "root";
    public static final String DEFAULT_PERMISSIONS = "rwxrwxrwx";

    private byte[] boot;
    private byte[] LBL; // 256 bloques en 1k
    private byte[] LIL; // 16 numeros de inodos libres
    private Inode[] inodeList; // son 4 bloques de 1K, 16 inodes por bloque = 64 inodes en total
    private Directory rootDirectory;

    public FSService() {
        initBoot();
        initLBL();
        initLIL();
        initInodeList();
        initRootDirectory();
    }

    private void initBoot() {
        log.info("Initializing boot ...");
        boot = new byte[1024];
        Arrays.fill(boot, (byte) 0);
    }

    private void initLBL() {
        log.info("Initializing LBL ...");
        LBL = new byte[1024];
        Arrays.fill(LBL, (byte) 0);
        LBL[0] = 9;
        LBL[1] = 10;
        LBL[2] = 11;
    }

    private void initLIL() {
        log.info("Initializing LIL ...");
        LIL = new byte[1024];
        Arrays.fill(LIL, (byte) 0);
        byte startInode = 3;
        for (byte i = 0; i <= 16; i++) {
            LIL[i] = startInode;
            startInode++;
        }
        LIL[0] = 3;
        LIL[1] = 4;
        LIL[2] = 5;
    }

    private void initInodeList() {
        log.info("Initializing Inodes list ...");
        inodeList = new Inode[64];
        log.info("Inode list size: {}", inodeList.length);
        for (int i = 0; i < inodeList.length; i++) {
            inodeList[i] = Inode.builder().size(0).type(FileType.REGULAR_FILE).owner(DEFAULT_OWNER).creationDate(new Date()).permissions(DEFAULT_PERMISSIONS).tableOfContents(new int[11]).build();
        }
    }

    private void initRootDirectory() {
        log.info("Initializing root directory ...");
        Inode rootDirectoryInode = Inode.builder()
                .size(1024).type(FileType.DIRECTORY).owner(DEFAULT_OWNER).creationDate(new Date()).permissions(DEFAULT_PERMISSIONS).tableOfContents(new int[11]).build();

        rootDirectory = new Directory(2, "root");
    }

    public void writeDiskFile() {
        RandomAccessFile diskFile = null;

        try {
            long offset = 0;
            log.info("Opening disk file in {} mode", FileAccessMode.READ_WRITE);
            diskFile = new RandomAccessFile(DISK_FILE_PATH, FileAccessMode.READ_WRITE.toString());

            // writing boot
            log.info("Writing boot into disk file with fd={}", diskFile.getFD().toString());
            diskFile.write(boot);
            offset += boot.length;
            diskFile.seek(offset);
            log.info("Setting file seek to={}", offset);

            // writing LIL
            log.info("Writing LIL into disk file with fd={}", diskFile.getFD().toString());
            diskFile.write(LIL);
            offset += LIL.length;
            diskFile.seek(offset);
            log.info("Setting file seek to={}", offset);

            // writing LBL
            log.info("Writing LBL into disk file with fd={}", diskFile.getFD().toString());
            diskFile.write(LBL);
            offset += LBL.length;
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
