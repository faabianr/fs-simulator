package com.mcc.fs.simulator.model.command;

import com.mcc.fs.simulator.model.users.User;
import com.mcc.fs.simulator.service.FSService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ShowFileCmd extends FSCommand {
    private static final String COMMAND = "showf";
    private static final String DESCRIPTION = "Displays the content of a file in current's location";
    private static final String USAGE = "showf <filename>";

    public ShowFileCmd() {
        super(COMMAND, DESCRIPTION, USAGE);
    }

    @Override
    public String execute(FSService fsService, String args, User user) {
        log.info("Executing command: {} with args: {}", getCommand(), args);
        if (args != null && !args.isEmpty()) {
            return fsService.showFile(args.trim(), user);
        } else {
            return "Please follow the next form: showf <filename>";
        }
    }
}