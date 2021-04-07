package com.mcc.fs.simulator.model.command;

import com.mcc.fs.simulator.service.FSService;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public abstract class FSCommand {
    private final String command;
    private final String description;
    private final String usage;

    public abstract String execute(FSService fsService, String args);
}
