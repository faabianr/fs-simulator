package com.mcc.fs.simulator.model.command;

import com.mcc.fs.simulator.model.users.User;
import com.mcc.fs.simulator.service.FSService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class GoToDirectoryCmd extends FSCommand {
    private static final String COMMAND = "gotodir";
    private static final String DESCRIPTION = "Go to a given directory";
    private static final String USAGE = "gotodir <dirname>";

    public GoToDirectoryCmd() {
        super(COMMAND, DESCRIPTION, USAGE);
    }

    @Override
    public String execute(FSService fsService, String args, User user) {
        log.info("Executing command: {} with args: {}", getCommand(), args);
        if (args != null && !args.isEmpty()) {
            return fsService.goToDir(args.trim(), user);
        } else {
            return "Please follow the next form: gotodir (dirname)";
        }
    }
}
