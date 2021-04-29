package com.mcc.fs.simulator.model.command;

import com.mcc.fs.simulator.model.users.User;
import com.mcc.fs.simulator.service.FSService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RemoveFileCmd extends FSCommand {
    private static final String COMMAND = "removef";
    private static final String DESCRIPTION = "Removes a file in current's location";
    private static final String USAGE = "removef <filename>";

    public RemoveFileCmd() {
        super(COMMAND, DESCRIPTION, USAGE);
    }

    @Override
    public String execute(FSService fsService, String args, User user) {
        log.info("Executing command: {} with args: {}", getCommand(), args);
        if (args != null && !args.isEmpty()) {
            return fsService.removeFile(args.trim(), user);
        } else {
            return "Please follow the next form: removef (filename)";
        }
    }
}