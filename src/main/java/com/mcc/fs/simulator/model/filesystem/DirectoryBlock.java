package com.mcc.fs.simulator.model.filesystem;

import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
public class DirectoryBlock extends Block {

    public static final int MAX_NUMBER_OF_ENTRIES = 64;

    private List<DirectoryEntry> entries;

    public DirectoryBlock() {
        // 1 directory contains 64 entries
        // 1 entry is a 16 bytes item: 2 bytes for inode number and 14 bytes for name
        entries = new ArrayList<>();
    }

    public List<DirectoryEntry> getEntries() {
        return entries;
    }

    public int getSize() {
        return entries.size() * DirectoryEntry.BYTES;
    }

    public void removeEntry(DirectoryEntry directoryEntry) {
        entries = entries.stream()
                .filter(entry -> entry.getInode() != directoryEntry.getInode())
                .collect(Collectors.toList());
    }

    public boolean addEntry(DirectoryEntry entry) {
        if (entries.size() < MAX_NUMBER_OF_ENTRIES) {
            entries.add(entry);
            return true;
        }
        return false;
    }

}
