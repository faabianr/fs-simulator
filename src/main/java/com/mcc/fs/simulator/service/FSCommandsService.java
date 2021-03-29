package com.mcc.fs.simulator.service;

import com.mcc.fs.simulator.model.commands.CreateDirectoryCmd;
import com.mcc.fs.simulator.model.commands.FSCommand;
import com.mcc.fs.simulator.model.commands.ListDirectoryCmd;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class FSCommandsService {

    private final List<FSCommand> commands;

    public FSCommandsService() {
        commands = Arrays.asList(new CreateDirectoryCmd(), new ListDirectoryCmd());
    }

    public List<FSCommand> getCommands() {
        return commands;
    }

}
