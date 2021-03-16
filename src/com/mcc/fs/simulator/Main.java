package com.mcc.fs.simulator;

public class Main {

    public static void main(String[] args) {
        FSService fsService = new FSService();
        fsService.writeDiskFile();
        fsService.readDiskFile();
    }
}
