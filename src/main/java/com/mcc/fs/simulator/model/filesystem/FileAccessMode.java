package com.mcc.fs.simulator.model.filesystem;

public enum FileAccessMode {

    READ("r"), WRITE("w"), READ_WRITE("rw");

    private final String mode;

    FileAccessMode(String mode) {
        this.mode = mode;
    }

    @Override
    public String toString() {
        return mode;
    }

}
