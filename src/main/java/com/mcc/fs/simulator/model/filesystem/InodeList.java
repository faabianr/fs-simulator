package com.mcc.fs.simulator.model.filesystem;

import com.mcc.fs.simulator.model.users.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@Slf4j
public class InodeList {
    public static final int BYTES = 4096;

    private Inode[] inodes; // son 4 bloques de 1K, 16 inodes por bloque = 64 inodes en total

    public void init(User defaultOwner) {
        log.info("Initializing Inodes list ...");
        inodes = new Inode[64];
        log.info("Inode list size: {}", inodes.length);
        for (int i = 0; i < inodes.length; i++) {
            inodes[i] = Inode.builder().size(0).type(FileType.FREE_INODE).owner(defaultOwner).creationDate(new Date()).permissions(FilePermission.RESTRICTED_TO_OWNER).tableOfContents(new int[11]).build();
        }
    }

    public void registerInode(Inode inode, int position) {
        log.info("Registering inode {}", position);
        inodes[position - 1] = inode;
    }

    public Inode getInodeByPosition(int position) {
        return inodes[position - 1];
    }

    public Inode[] getInodes() {
        return inodes;
    }

}
