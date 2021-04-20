package com.mcc.fs.simulator.model.command;

import com.mcc.fs.simulator.service.FSService;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
public class ListDirectoryCmd extends FSCommand {
    private static final String COMMAND = "listdir";
    private static final String DESCRIPTION = "Lists the content of the current directory";
    private static final String USAGE = "listdir";

    public ListDirectoryCmd() {
        super(COMMAND, DESCRIPTION, USAGE);
    }

    @Override
    public String execute(FSService fsService, String args) {
        log.info("Executing command: {} with args: {}", getCommand(), args);
        String directories = fsService.listdir();
        // TODO implement method logic, this is just a sample return
        return directories;
    }

}
