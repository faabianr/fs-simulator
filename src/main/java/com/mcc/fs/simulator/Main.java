package com.mcc.fs.simulator;

import com.mcc.fs.simulator.model.helper.DiskHelper;
import com.mcc.fs.simulator.model.users.User;
import com.mcc.fs.simulator.service.FSService;
import com.mcc.fs.simulator.service.UsersService;

public class Main {

    public static void main(String[] args) {
        UsersService usersService = new UsersService();
        FSService fsService = new FSService(usersService, new DiskHelper(usersService));

        User user = usersService.registerUser("fabian");

        fsService.writeDiskFile();
        fsService.createDir("mydir", user);
        fsService.createDir("testdirectory", user);
        System.out.println(fsService.listDir(null, user));
        String createOutput = fsService.CreateFile("testfile", "contentdasdsadsa", user);
        System.out.println("create file: " + createOutput);
        System.out.println(fsService.listDir(null, user));
    }

}
