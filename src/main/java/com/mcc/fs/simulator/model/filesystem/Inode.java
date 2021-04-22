package com.mcc.fs.simulator.model.filesystem;

import lombok.Builder;
import lombok.Data;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Date;

@Builder
@Data
public class Inode { // 1 Inode = 64 bytes
    private int size; // 4 bytes (considering an int)
    private FileType type; // 2 bytes (considering a char)
    private String owner; // 4 bytes (considering an int)
    private Date creationDate; // 8 bytes (considering a long which can be used to parse dates)
    private String permissions; // 2 bytes (connsidering a short)
    private int[] tableOfContents; // (considering 4 bytes for each int) = 44 bytes

    public byte[] toByteArray() {
        byte[] bytes = new byte[64];

        ByteBuffer byteBuffer = ByteBuffer.wrap(bytes);

        byteBuffer.putInt(size);
        byteBuffer.putChar(type.toString().charAt(0));
        byteBuffer.putInt(owner.hashCode()); // TODO review if this convertion is correct
        byteBuffer.putLong(creationDate.getTime());
        byteBuffer.putShort((short) 1234); // TODO implement this convertion

        for (int entry : tableOfContents) {
            byteBuffer.putInt(entry);
        }

        return bytes;
    }

    public static Inode parseInode(byte[] bytes) {
        // TODO implement this
        return null;
    }

    public static void main(String[] args) throws IOException {
        byte[] bytes = new byte[64];

        int size = 5;
        char type = '-';
        int owner = 2;
        long date = new Date().getTime();
        short permissions = 1234;
        int[] tableOfContents = new int[11];

        Arrays.fill(tableOfContents, 2);

        System.out.println("writing byte array");

        ByteBuffer byteBuffer = ByteBuffer.wrap(bytes);

        byteBuffer.putInt(size);
        byteBuffer.putChar(type);
        byteBuffer.putInt(owner);
        byteBuffer.putLong(date);
        byteBuffer.putShort(permissions);

        for (int entry : tableOfContents) {
            byteBuffer.putInt(entry);
        }

        System.out.println("reading byte array");

        DataInputStream in = new DataInputStream(new ByteArrayInputStream(bytes));

        int readSize = in.readInt();
        char readType = in.readChar();
        int readOwner = in.readInt();
        long readDate = in.readLong();
        short readPermissions = in.readShort();
        int[] readTableOfContents = new int[11];

        for (int i = 0; i < readTableOfContents.length; i++) {
            readTableOfContents[i] = in.readInt();
        }

        System.out.println("readSize: " + readSize);
        System.out.println("readType: " + readType);
        System.out.println("readOwner: " + readOwner);
        System.out.println("readDate: " + readDate);
        System.out.println("readSize: " + readSize);
        System.out.println("readPermissions: " + readPermissions);
        System.out.println("readTableOfContents: " + Arrays.toString(readTableOfContents));

    }

}
