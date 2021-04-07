package com.mcc.fs.simulator.service;

import com.mcc.fs.simulator.exception.InvalidCommandException;
import com.mcc.fs.simulator.model.command.CreateDirectoryCmd;
import com.mcc.fs.simulator.model.command.FSCommand;
import com.mcc.fs.simulator.model.command.ListDirectoryCmd;
import com.mcc.fs.simulator.model.network.ExecutionRequest;
import com.mcc.fs.simulator.model.network.ExecutionResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class FSCommandsService {

    private final Map<String, FSCommand> commandsMap;

    private final FSService fsService;

    public FSCommandsService(FSService fsService) {
        this.fsService = fsService;

        commandsMap = new HashMap<>();

        // Registering commands
        registerCommand(new CreateDirectoryCmd());
        registerCommand(new ListDirectoryCmd());
    }

    private void registerCommand(FSCommand fsCommand) {
        log.debug("Registering command: {}", fsCommand.getCommand());
        commandsMap.put(fsCommand.getCommand(), fsCommand);
    }

    public List<FSCommand> getCommands() {
        return new ArrayList<>(commandsMap.values());
    }

    public ExecutionResponse executeCommand(ExecutionRequest executionRequest) {
        String command;
        String args;

        // taking the first word as command and others as args
        String[] inputParts = executionRequest.getInput().split(" ");

        command = inputParts[0];
        args = executionRequest.getInput().replace(command, "");

        log.debug("originalInput={}, command: {}, args: {}", executionRequest.getInput(), command, args);

        // Checking if the command is valid
        if (!commandsMap.containsKey(command)) {
            log.error("Invalid command: {}", command);
            throw new InvalidCommandException();
        }

        // Executing the command
        FSCommand fsCommand = commandsMap.get(command); // getting the command instance to use based on command name
        String output = fsCommand.execute(fsService, args);

        return ExecutionResponse.builder().output(output).build();
    }

}
