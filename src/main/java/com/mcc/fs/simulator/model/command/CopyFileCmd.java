package com.mcc.fs.simulator.model.command;

import com.mcc.fs.simulator.model.users.User;
import com.mcc.fs.simulator.service.FSService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CopyFileCmd extends FSCommand {
    private static final String COMMAND = "copyf";
    private static final String DESCRIPTION = "Copy a file in current's location";
    private static final String USAGE = "copyf <filename> <dirpath>";

    public CopyFileCmd() {
        super(COMMAND, DESCRIPTION, USAGE);
    }

    @Override
    public String execute(FSService fsService, String args, User user) {
        log.info("Executing command: {} with args: {}", getCommand(), args);
        if (args != null && !args.isEmpty()) {
            fsService.CopyFile();
        } else {
            return "Please follow the next form: copyf (filename) (dirpath)";
        }
        // TODO implement method logic, this is just a sample return
        return "file " + args + " copied.\n";
    }
}