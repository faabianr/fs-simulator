package com.mcc.fs.simulator.model.commands;

public class CreateDirectoryCmd extends FSCommand {
    private static final String COMMAND = "createdir";
    private static final String DESCRIPTION = "Creates a directory in current's location";
    private static final String USAGE = "cratedir <dirname>";

    public CreateDirectoryCmd() {
        super(COMMAND, DESCRIPTION, USAGE);
    }

}
