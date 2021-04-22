package com.mcc.fs.simulator.model.filesystem;
        
        import lombok.AllArgsConstructor;
        import lombok.Builder;
        import lombok.Data;
        import lombok.NoArgsConstructor;
        import lombok.extern.slf4j.Slf4j;

        import java.io.FileNotFoundException;
        import java.io.IOException;
        import java.io.RandomAccessFile;
        
@AllArgsConstructor
@NoArgsConstructor
@Data
@Slf4j

public abstract class Disk {
    public static final String DISK_FILE_PATH = "./diskfile.txt";
    public static RandomAccessFile diskFile = null;
    protected byte[] content;
    
    public void writeToDisk(byte[] content, int seek) {
        // TODO implement
        try {
            
            diskFile = new RandomAccessFile(DISK_FILE_PATH, FileAccessMode.READ_WRITE.toString());
            seek = seek * 1024;
            diskFile.seek(seek);
            diskFile.write(content);
            } catch (FileNotFoundException e) {
                log.error("Unable to create disk file. Cause: {}", e.getMessage(), e);
                System.exit(1);
            } catch (IOException e) {
                log.error("Unable to write to disk file. Cause: {}", e.getMessage(), e);
                System.exit(1);
            }finally {
            if (diskFile != null) {
                try {
                    log.info("Closing disk file with fd={}", diskFile.getFD().toString());
                    diskFile.close();
                } catch (IOException e) {
                    log.error("Unable to close diskfile", e);
                }
            }
        }
        log.info("Writing content to disk.");
    }
    
    public void Read() {
        log.info("Reading content from disk");
        try {
        diskFile = new RandomAccessFile(DISK_FILE_PATH, FileAccessMode.READ.toString());
        byte[] content = new byte[(int) diskFile.length()];
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
        // TODO implement
    }
}
