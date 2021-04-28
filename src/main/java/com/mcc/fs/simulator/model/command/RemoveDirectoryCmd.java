package com.mcc.fs.simulator.model.command;

import com.mcc.fs.simulator.model.users.User;
import com.mcc.fs.simulator.service.FSService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RemoveDirectoryCmd extends FSCommand {
    private static final String COMMAND = "removedir";
    private static final String DESCRIPTION = "Remove a directory in current's location";
    private static final String USAGE = "removedir <dirname>";

    public RemoveDirectoryCmd() {
        super(COMMAND, DESCRIPTION, USAGE);
    }

    @Override
    public String execute(FSService fsService, String args, User user) {
        log.info("Executing command: {} with args: {}", getCommand(), args);
        if (args != null && !args.isEmpty()) {
            fsService.RemoveDir();
        } else {
            return "Please follow the next form: removedir (dirname)";
        }
        // TODO implement method logic, this is just a sample return
        return "directory " + args + " removed.\n";
    }
}