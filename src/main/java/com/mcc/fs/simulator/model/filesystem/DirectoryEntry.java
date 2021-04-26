package com.mcc.fs.simulator.model.filesystem;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@ToString
public class DirectoryEntry {
    // directory entry = 16 bytes: 2 bytes for inode number and 14 bytes for name
    private short inode;
    private String name;
}
