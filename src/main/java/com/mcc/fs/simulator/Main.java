package com.mcc.fs.simulator;

import com.mcc.fs.simulator.model.filesystem.FilePermission;
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
        fsService.createDir("mydir", user, FilePermission.OTHERS_CAN_READ);
        fsService.createDir("testdirectory", user, FilePermission.OTHERS_CAN_READ);
        System.out.println(fsService.listDir(null, user));
        String createOutput = fsService.createFile("testfile", "contentdasdsadsa", user, FilePermission.OTHERS_CAN_READ);
        System.out.println("create file: " + createOutput);
        System.out.println(fsService.listDir(null, user));
        System.out.println("content of file: " + fsService.showFile("testfile", user));
        fsService.copyFile("testfile", "testdirectory", user);
        System.out.println(fsService.listDir("testdirectory", user));
    }

}
