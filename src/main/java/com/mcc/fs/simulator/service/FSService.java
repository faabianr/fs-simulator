package com.mcc.fs.simulator.service;

import com.mcc.fs.simulator.model.filesystem.Directory;
import com.mcc.fs.simulator.model.filesystem.FileAccessMode;
import com.mcc.fs.simulator.model.filesystem.Inode;
import org.springframework.stereotype.Service;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Service
public class FSService {

    private static final String DISK_FILE_PATH = "./diskfile";
    private byte[] boot;
    private byte[] LBL; // 256 bloques en 1k
    private byte[] LIL; // 16 numeros de inodos libres
    private byte[] InodeTable; // son 4 bloques de 1024.
    private Directory rootDirectory;

    public FSService() {
        initBoot();
        initLBL();
        initLIL();
        initInodeTable();
        initRootDirectory();
    }

    private void initBoot() {
        System.out.println("Initializing boot ...");
        boot = new byte[1024];
        Arrays.fill(boot, (byte) 0);
    }

    private void initLBL() {
        System.out.println("Initializing LBL ...");
        LBL = new byte[1024];
        Arrays.fill(LBL, (byte) 0);
        LBL[0] = 9;
        LBL[1] = 10;
        LBL[2] = 11;
    }

    private void initLIL() {
        System.out.println("Initializing LIL ...");
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

    private void initInodeTable() {
        System.out.println("Initializing Inode Table ...");
        InodeTable = new byte[1024 * 4];
        System.out.println("Inode table size:" + InodeTable.length);
        Arrays.fill(InodeTable, (byte) 0);
    }

    private void initRootDirectory() {
        System.out.println("Initializing root directory ...");
        Inode rootDirectoryInode = Inode.builder()
                .size(1024).type("D").owner("0").creationDate(new Date()).permissions("rwxrwx").tableOfContents(new int[11]).build();

        rootDirectory = new Directory(2, "root");
    }
    
    public String listdir(){
        String list= ".<br/>..";
        return list;
    }
    
    public String CreateDir(){
        String created= ".<br/>..";
        return created;
    }
    
    public String RemoveDir(){
        String removed= ".<br/>..";
        return removed;
    }
    public String MoveDir(){
        String moveto= ".<br/>..";
        return moveto;
    }
    public String CreateFile(){
        String craetef= ".<br/>..";
        return craetef;
    }
    public String RemoveFile(){
        String revomed= ".<br/>..";
        return revomed;
    }
    public String MoveFile(){
        String movef= ".<br/>..";
        return movef;
    }
    public String CopyFile(){
        String copyf= ".<br/>..";
        return copyf;
    }
    
    public void writeDiskFile() {
        try {
            long offset = 0;
            System.out.println("Opening disk file in " + FileAccessMode.READ_WRITE.toString() + " mode.");
            RandomAccessFile diskFile = new RandomAccessFile(DISK_FILE_PATH, FileAccessMode.READ_WRITE.toString());

            // writing boot
            System.out.println("Writing boot into disk file with fd=" + diskFile.getFD().toString());
            diskFile.write(boot);
            offset += boot.length;
            diskFile.seek(offset);
            System.out.println("Setting file seek to=" + offset);

            // writing LIL
            System.out.println("Writing LIL into disk file with fd=" + diskFile.getFD().toString());
            diskFile.write(LIL);
            offset += LIL.length;
            diskFile.seek(offset);
            System.out.println("Setting file seek to=" + offset);

            // writing LBL
            System.out.println("Writing LBL into disk file with fd=" + diskFile.getFD().toString());
            diskFile.write(LBL);
            offset += LBL.length;
            diskFile.seek(offset);
            System.out.println("Setting file seek to=" + offset);

            // writing Inode Table
            System.out.println("Writing Inode Table into disk file with fd=" + diskFile.getFD().toString());
            diskFile.write(InodeTable);
            offset += InodeTable.length;
            diskFile.seek(offset);
            System.out.println("Setting file seek to=" + offset);

            // writing root directory
            System.out.println("Writing inode root directory into disk file with fd=" + diskFile.getFD().toString());
            offset = (LBL.length * 3L) + 1; // es +1 porque el primer inodo no se usa
            System.out.println("offset=" + offset);
            diskFile.seek(offset);
            diskFile.write(rootDirectory.getInode());
            System.out.println("Setting file seek to=" + offset);

            System.out.println("Writing content root directory into disk file with fd=" + diskFile.getFD().toString());
            offset += InodeTable.length - 2; // Le quitamos el 1 que contamos en el offset del inode root
            System.out.println("offset=" + offset);
            diskFile.seek(offset);
            diskFile.writeChar('.');
            diskFile.writeChars("..");
            System.out.println("Setting file seek to=" + offset);

            System.out.println("Closing disk file with fd=" + diskFile.getFD().toString());
            diskFile.close();

        } catch (FileNotFoundException e) {
            System.err.println("Unable to create disk file. Cause: " + e.getMessage());
            System.exit(1);
        } catch (IOException e) {
            System.err.println("Unable to write to disk file. Cause: " + e.getMessage());
            System.exit(1);
        }

    }

    public void readDiskFile() {
        try {
            System.out.println("Opening disk file in " + FileAccessMode.READ.toString() + " mode.");
            RandomAccessFile diskFile = new RandomAccessFile(DISK_FILE_PATH, FileAccessMode.READ.toString());
            byte[] content = new byte[(int) diskFile.length()];
            System.out.println("Reading disk file");
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

            System.out.println("\nClosing disk file with fd=" + diskFile.getFD().toString());
            diskFile.close();
        } catch (FileNotFoundException e) {
            System.err.println("Unable to read disk file. Not found.");
            System.exit(1);
        } catch (IOException e) {
            System.err.println("Unable to read disk file. Cause: " + e.getMessage());
            System.exit(1);
        }
    }

}
