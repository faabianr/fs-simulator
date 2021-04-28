package com.mcc.fs.simulator.model.command;

import com.mcc.fs.simulator.service.FSService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CreateDirectoryCmd extends FSCommand {
    private static final String COMMAND = "createdir";
    private static final String DESCRIPTION = "Creates a directory in current's location";
    private static final String USAGE = "createdir <dirname>";

    public CreateDirectoryCmd() {
        super(COMMAND, DESCRIPTION, USAGE);
    }

    @Override
    public String execute(FSService fsService, String args) {
        log.info("Executing command: {} with args: {}", getCommand(), args);
        if (args != null && !args.isEmpty()) {
            return fsService.createDir(args);
        } else {
            return "Please follow the next form: createdir (dirname)";
        }
    }
}
