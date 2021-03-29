package com.mcc.fs.simulator.model.commands;

public class ListDirectoryCmd extends FSCommand {
    private static final String COMMAND = "listdir";
    private static final String DESCRIPTION = "Lists the content of the current directory";
    private static final String USAGE = "listdir";

    public ListDirectoryCmd() {
        super(COMMAND, DESCRIPTION, USAGE);
    }
    
}
