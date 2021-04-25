package com.mcc.fs.simulator.model.helper;

import com.mcc.fs.simulator.model.filesystem.FilePermission;
import com.mcc.fs.simulator.model.filesystem.FileType;
import com.mcc.fs.simulator.model.filesystem.Inode;
import com.mcc.fs.simulator.service.UsersService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Date;

/**
 * Helper service used to perform disk-related operations.
 */
@Slf4j
@Service
public class DiskHelper {

    private final UsersService usersService;

    public DiskHelper(UsersService usersService) {
        this.usersService = usersService;
    }

    /**
     * Converts a {@link Inode} object into an array of bytes.
     *
     * @param inode the inode object to convert.
     * @return a byte array representing the given object.
     */
    public byte[] inodeToByteArray(Inode inode) {
        log.info("converting inode to byte arrray with info={}", inode);

        byte[] bytes = new byte[Inode.BYTES];

        ByteBuffer byteBuffer = ByteBuffer.wrap(bytes);

        byteBuffer.putInt(inode.getSize());
        byteBuffer.putChar(inode.getType().getValue());
        byteBuffer.putInt(inode.getOwner().getId());
        byteBuffer.putLong(inode.getCreationDate().getTime());
        byteBuffer.putShort(inode.getPermissions().getValue());

        for (int entry : inode.getTableOfContents()) {
            byteBuffer.putInt(entry);
        }

        return bytes;
    }

    /**
     * Creates an instance of an {@link Inode} class from an array of bytes.
     *
     * @param bytes the bytes array used to create the inode.
     * @return an instance of an inode.
     */
    public Inode byteArrayToInode(byte[] bytes) throws IOException {
        DataInputStream in = new DataInputStream(new ByteArrayInputStream(bytes));

        int sizeValue = in.readInt();
        char typeValue = in.readChar();
        int ownerValue = in.readInt();
        long dateValue = in.readLong();
        short permissionsValue = in.readShort();
        int[] tableOfContentsValue = new int[11];

        for (int i = 0; i < tableOfContentsValue.length; i++) {
            tableOfContentsValue[i] = in.readInt();
        }

        Inode inode = Inode.builder() //
                .size(sizeValue) //
                .type(FileType.toFileType(typeValue)) //
                .owner(usersService.getUserById(ownerValue)) //
                .creationDate(new Date(dateValue)) //
                .permissions(FilePermission.toFilePermission(permissionsValue)) //
                .tableOfContents(tableOfContentsValue) //
                .build();

        log.info("inode from bytearray: {}", inode.toString());

        return inode;
    }

}
