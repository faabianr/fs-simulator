package com.mcc.fs.simulator.model.filesystem;

public enum FileType {

    DIRECTORY("d"), REGULAR_FILE("-"), FREE_INODE("0");

    private final String type;

    FileType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return type;
    }

}
