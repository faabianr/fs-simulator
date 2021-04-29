package com.mcc.fs.simulator.service;

import com.mcc.fs.simulator.exception.InvalidCommandException;
import com.mcc.fs.simulator.model.command.*;
import com.mcc.fs.simulator.model.network.ExecutionRequest;
import com.mcc.fs.simulator.model.network.ExecutionResponse;
import com.mcc.fs.simulator.model.users.User;
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
    private final UsersService usersService;

    public FSCommandsService(FSService fsService, UsersService usersService) {
        this.fsService = fsService;
        this.usersService = usersService;

        commandsMap = new HashMap<>();

        // Registering commands
        registerCommand(new CreateDirectoryCmd());
        registerCommand(new ListDirectoryCmd());
        registerCommand(new RemoveDirectoryCmd());
        registerCommand(new MoveDirectoryCmd());
        registerCommand(new CreateFileCmd());
        registerCommand(new RemoveFileCmd());
        registerCommand(new MoveFileCmd());
        registerCommand(new CopyFileCmd());
        registerCommand(new GoToDirectoryCmd());
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
        User requestUser = usersService.getUserById(executionRequest.getUserId());
        String output = fsCommand.execute(fsService, args, requestUser);

        return ExecutionResponse.builder().output(output).build();
    }

}
