package com.mcc.fs.simulator;

import com.mcc.fs.simulator.service.FSService;

public class Main {

    public static void main(String[] args) {
        FSService fsService = new FSService();
        fsService.writeDiskFile();
        fsService.readDiskFile();
    }
}
