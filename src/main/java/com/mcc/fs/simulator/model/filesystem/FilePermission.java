package com.mcc.fs.simulator.model.filesystem;

import java.util.HashMap;
import java.util.Map;

public enum FilePermission {

    RESTRICTED_TO_OWNER((short) 1), OTHERS_CAN_READ((short) 2), OTHERS_CAN_READ_WRITE((short) 3);

    private static final Map<Short, FilePermission> SHORT_FILE_PERMISSION_MAP = new HashMap<>();

    static {
        for (FilePermission filePermission : FilePermission.values()) {
            SHORT_FILE_PERMISSION_MAP.put(filePermission.value, filePermission);
        }
    }

    private final short value;

    FilePermission(short value) {
        this.value = value;
    }

    public short getValue() {
        return value;
    }

    public static FilePermission toFilePermission(short value) {
        return SHORT_FILE_PERMISSION_MAP.get(value);
    }

    @Override
    public String toString() {
        return this.name() + " - " + value;
    }

}
