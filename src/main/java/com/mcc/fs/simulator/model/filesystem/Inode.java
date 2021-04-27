package com.mcc.fs.simulator.model.filesystem;

import com.mcc.fs.simulator.model.users.User;
import lombok.Builder;
import lombok.Data;

import java.util.Date;

@Builder
@Data
public class Inode {

    public static final int BYTES = 64;

    private int size; // 4 bytes (considering an int)
    private FileType type; // 2 bytes (considering a char)
    private User owner; // 4 bytes (considering an int)
    private Date creationDate; // 8 bytes (considering a long which can be used to parse dates)
    private FilePermission permissions; // 2 bytes (connsidering a short)
    private int[] tableOfContents; // (considering 4 bytes for each int) = 44 bytes

}
