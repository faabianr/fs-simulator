package com.mcc.fs.simulator.model.filesystem;

import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public class DirectoryBlock extends Block {

    private static final int MAX_ALLOWED_ENTRIES = 64;

    private final List<DirectoryEntry> entries;

    public DirectoryBlock() {
        // 1 directory contains 64 entries
        // 1 entry is a 16 bytes item: 2 bytes for inode number and 14 bytes for name
        entries = new ArrayList<>();
    }

    public void addEntry(DirectoryEntry entry) {
        if (entries.size() < MAX_ALLOWED_ENTRIES) {
            entries.add(entry);
        } else {
            log.error("unable to add entry, the dir has reached the maximum amount of entries.");
        }
    }

}
