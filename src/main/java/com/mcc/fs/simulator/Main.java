package com.mcc.fs.simulator;

import com.mcc.fs.simulator.service.FSService;
import com.mcc.fs.simulator.service.UsersService;

public class Main {

    public static void main(String[] args) {
        FSService fsService = new FSService(new UsersService());
        fsService.writeDiskFile();
        fsService.readDiskFile();
    }
}
