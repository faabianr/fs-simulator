package com.mcc.fs.simulator.model.filesystem;

import java.util.HashMap;
import java.util.Map;

public enum FileType {

    DIRECTORY('d'), REGULAR_FILE('-'), FREE_INODE('0');

    private static final Map<Character, FileType> CHARACTER_FILE_TYPE_MAP = new HashMap<>();

    private final char value;

    static {
        for (FileType fileType : FileType.values()) {
            CHARACTER_FILE_TYPE_MAP.put(fileType.value, fileType);
        }
    }

    FileType(char value) {
        this.value = value;
    }

    public Character getValue() {
        return value;
    }

    public static FileType toFileType(char value) {
        return CHARACTER_FILE_TYPE_MAP.get(value);
    }

    @Override
    public String toString() {
        return Character.toString(value);
    }

}
