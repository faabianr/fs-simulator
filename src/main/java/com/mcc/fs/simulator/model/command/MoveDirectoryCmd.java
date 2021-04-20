package com.mcc.fs.simulator.model.command;

import com.mcc.fs.simulator.service.FSService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MoveDirectoryCmd extends FSCommand {
    private static final String COMMAND = "movedir";
    private static final String DESCRIPTION = "Moves a directory in current's location to another location";
    private static final String USAGE = "movedir <dirname> <newlocation>";
    
    public MoveDirectoryCmd() {
        super(COMMAND, DESCRIPTION, USAGE);
    }
    
    @Override
    public String execute(FSService fsService, String args) {
        log.info("Executing command: {} with args: {}", getCommand(), args);
        if (args != null && !args.isEmpty()) {
            fsService.MoveDir();
        }else {
            return "Please follow the next form: movedir (dirname) (newlocation)";
        }
        // TODO implement method logic, this is just a sample return
        return "directory " + args + "moved.\n" ;
    }
}