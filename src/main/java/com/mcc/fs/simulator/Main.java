package com.mcc.fs.simulator;

import com.mcc.fs.simulator.model.helper.DiskHelper;
import com.mcc.fs.simulator.service.FSService;
import com.mcc.fs.simulator.service.UsersService;

public class Main {

    public static void main(String[] args) {
        UsersService usersService = new UsersService();
        FSService fsService = new FSService(usersService, new DiskHelper(usersService));
        fsService.writeDiskFile();
    }
}
