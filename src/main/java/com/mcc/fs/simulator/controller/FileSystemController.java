package com.mcc.fs.simulator.controller;

import com.mcc.fs.simulator.model.command.FSCommand;
import com.mcc.fs.simulator.model.network.ExecutionRequest;
import com.mcc.fs.simulator.model.network.ExecutionResponse;
import com.mcc.fs.simulator.service.FSCommandsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/filesystem/commands")
public class FileSystemController {

    private final FSCommandsService fsCommandsService;

    public FileSystemController(FSCommandsService fsCommandsService) {
        this.fsCommandsService = fsCommandsService;
    }

    @GetMapping()
    public ResponseEntity<List<FSCommand>> getCommands() {
        log.info("Returning available actions: {}", fsCommandsService.getCommands());
        return ResponseEntity.ok(fsCommandsService.getCommands());
    }

    @PostMapping
    public ResponseEntity<ExecutionResponse> executeCommand(ExecutionRequest executionRequest) {
        log.info("Received input: {}", executionRequest.getInput());

        // executing the command
        ExecutionResponse executionResponse = fsCommandsService.executeCommand(executionRequest);

        log.info("Executed '{}' with output: {}", executionRequest.getInput(), executionResponse.getOutput());

        return ResponseEntity.ok(executionResponse);
    }

}
