package com.mcc.fs.simulator.model.command;

import com.mcc.fs.simulator.model.users.User;
import com.mcc.fs.simulator.service.FSService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CreateFileCmd extends FSCommand {
    private static final String COMMAND = "createf";
    private static final String DESCRIPTION = "Creates a file in current's location";
    private static final String USAGE = "createf <filename>";

    public CreateFileCmd() {
        super(COMMAND, DESCRIPTION, USAGE);
    }

    @Override
    public String execute(FSService fsService, String args, User user) {
        log.info("Executing command: {} with args: {}", getCommand(), args);
        if (args != null && !args.isEmpty()) {
            fsService.CreateFile();
        } else {
            return "Please follow the next form: createf (filename)";
        }
        // TODO implement method logic, this is just a sample return
        return "File" + args + " created.\n";
    }
}