package com.mcc.fs.simulator.model.command;

import com.mcc.fs.simulator.model.users.User;
import com.mcc.fs.simulator.service.FSService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MoveFileCmd extends FSCommand {
    private static final String COMMAND = "movef";
    private static final String DESCRIPTION = "Moves a file in current's location to another location";
    private static final String USAGE = "movef <filename> <newlocation>";

    public MoveFileCmd() {
        super(COMMAND, DESCRIPTION, USAGE);
    }

    @Override
    public String execute(FSService fsService, String args, User user) {
        log.info("Executing command: {} with args: {}", getCommand(), args);
        if (args != null && !args.isEmpty()) {
            String[] argsArray = args.trim().split(" ");
            return fsService.moveFile(argsArray[0], argsArray[1], user);
        } else {
            return "Please follow the next form: movef (filename) (newlocation)";
        }
    }
}