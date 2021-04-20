package com.mcc.fs.simulator.model.command;

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
    public String execute(FSService fsService, String args) {
        log.info("Executing command: {} with args: {}", getCommand(), args);
        if (args != null && !args.isEmpty()) {
            fsService.MoveFile();
        }else {
            return "Please follow the next form: movef (filename) (newlocation)";
        }
        // TODO implement method logic, this is just a sample return
        return "file " + args + " moved.\n";
    }
}