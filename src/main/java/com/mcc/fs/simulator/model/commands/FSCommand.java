package com.mcc.fs.simulator.model.commands;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public abstract class FSCommand {
    private final String command;
    private final String description;
    private final String usage;
}
