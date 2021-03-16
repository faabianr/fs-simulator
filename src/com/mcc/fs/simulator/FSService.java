package com.mcc.fs.simulator;

import com.mcc.fs.simulator.model.FileAccessMode;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Arrays;

public class FSService {

    private static final String DISK_FILE_PATH = "./diskfile";
    private byte[] boot;
    private byte[] LBL;
    private byte[] LIL;

    public FSService() {
        initBoot();
        initLBL();
        initLIL();
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
        LBL[0] = 11;
        LBL[1] = 10;
        LBL[2] = 9;
    }

    private void initLIL() {
        System.out.println("Initializing LIL ...");
        LIL = new byte[1024];
        Arrays.fill(LIL, (byte) 0);
        LIL[0] = 3;
        LIL[1] = 4;
        LIL[2] = 5;
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

            // writing LBL
            System.out.println("Writing LBL into disk file with fd=" + diskFile.getFD().toString());
            diskFile.write(LBL);
            offset += LBL.length;
            diskFile.seek(offset);
            System.out.println("Setting file seek to=" + offset);

            // writing LIL
            System.out.println("Writing LIL into disk file with fd=" + diskFile.getFD().toString());
            diskFile.write(LIL);
            offset += LIL.length;
            diskFile.seek(offset);
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
            byte[] content = new byte[1024 * 3];

            System.out.println("Opening disk file in " + FileAccessMode.READ.toString() + " mode.");
            RandomAccessFile diskFile = new RandomAccessFile(DISK_FILE_PATH, FileAccessMode.READ.toString());
            System.out.println("Reading disk file");
            diskFile.read(content, 0, content.length);

            for (byte b : content) {
                System.out.print(b);
            }

            System.out.println("Closing disk file with fd=" + diskFile.getFD().toString());
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
