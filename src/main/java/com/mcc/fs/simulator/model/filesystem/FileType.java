package com.mcc.fs.simulator.model.filesystem;

public enum FileType {

    DIRECTORY("d"), REGULAR_FILE("-"), CHARACTER("c"), PIPE("p"), LINK("l"), BLOCK("b");

    private final String type;

    FileType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return type;
    }

}
