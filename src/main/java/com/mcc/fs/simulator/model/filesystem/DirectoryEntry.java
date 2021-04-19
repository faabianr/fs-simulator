package com.mcc.fs.simulator.model.filesystem;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class DirectoryEntry {
    // directory entry = 16 bytes: 2 bytes for inode number and 14 bytes for name
    private byte inode;
    private String name;
}
