package com.mcc.fs.simulator.model.filesystem;

import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public class DirectoryBlock extends Block {

    public static final int NUMBER_OF_ENTRIES = 64;

    private final List<DirectoryEntry> entries;

    public DirectoryBlock() {
        // 1 directory contains 64 entries
        // 1 entry is a 16 bytes item: 2 bytes for inode number and 14 bytes for name
        entries = new ArrayList<>();
    }

    public List<DirectoryEntry> getEntries() {
        return entries;
    }

    public void addEntry(DirectoryEntry entry) {
        entries.add(entry);
    }

}
