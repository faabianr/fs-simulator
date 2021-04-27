package com.mcc.fs.simulator.model.helper;

import com.mcc.fs.simulator.exception.TooLargeFileException;
import com.mcc.fs.simulator.model.filesystem.*;
import com.mcc.fs.simulator.service.UsersService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
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

    /**
     * Returns the {@link String} value of a block's content.
     *
     * @param block the block that will be read and converted into string.
     * @return an string representing the block's content.
     */
    public String blockContentToString(Block block) {
        return new String(block.getContent(), StandardCharsets.UTF_8);
    }

    /**
     * Creates an instance of a {@link Block} using the given content value.
     *
     * @param content the value of the content.
     * @return a block instance.
     */
    public Block contentToBlock(String content) {
        log.info("converting content={} to a block object", content);
        byte[] bytes = content.getBytes(StandardCharsets.UTF_8);

        if (bytes.length > Block.BYTES) {
            log.error("the file is too large to be stored (size={})", bytes.length);
            throw new TooLargeFileException();
        }

        return Block.builder().content(bytes).build();
    }

    /**
     * Creates a byte array that represents the given {@link DirectoryBlock} object.
     *
     * @param directoryBlock the directory block to convert to byte array.
     * @return a byte array representing the given input object.
     */
    public byte[] directoryBlockToByteArray(DirectoryBlock directoryBlock) {
        byte[] bytes = new byte[DirectoryBlock.BYTES];

        ByteBuffer byteBuffer = ByteBuffer.wrap(bytes);

        for (DirectoryEntry entry : directoryBlock.getEntries()) {
            byteBuffer.putShort(entry.getInode());

            byte[] nameBytes = new byte[14];
            byte[] originalNameBytes = entry.getName().getBytes(StandardCharsets.UTF_8);

            for (int i = 0; i < nameBytes.length; i++) {
                if (nameBytes[i] == (byte) 0) {
                    nameBytes[i] = 32;
                }
            }

            System.arraycopy(originalNameBytes, 0, nameBytes, 0, originalNameBytes.length);
            byteBuffer.put(nameBytes);
        }

        return bytes;
    }

    /**
     * Creates an instance of a {@link DirectoryBlock} from the given byte array.
     *
     * @param bytes the byte array used to create the directory block.
     * @return an instance of directory block.
     */
    public DirectoryBlock byteArrayToDirectoryBlock(byte[] bytes) throws IOException {
        DirectoryBlock directoryBlock = new DirectoryBlock();

        int offset = 0;

        for (int i = 0; i < DirectoryBlock.NUMBER_OF_ENTRIES; i++) {
            DirectoryEntry directoryEntry = new DirectoryEntry();

            byte[] inodeBytes = new byte[Short.BYTES];
            byte[] nameBytes = new byte[14];

            System.arraycopy(bytes, offset, inodeBytes, 0, inodeBytes.length);
            System.arraycopy(bytes, offset + 2, nameBytes, 0, nameBytes.length);

            DataInputStream inodeInputStream = new DataInputStream(new ByteArrayInputStream(inodeBytes));

            directoryEntry.setInode(inodeInputStream.readShort());
            directoryEntry.setName(new String(nameBytes, StandardCharsets.UTF_8).trim());

            directoryBlock.addEntry(directoryEntry);

            log.info("new directory entry added={}", directoryEntry);

            offset += DirectoryEntry.BYTES;
        }

        return directoryBlock;
    }

}
