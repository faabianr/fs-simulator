package com.mcc.fs.simulator.controller;

import com.mcc.fs.simulator.model.commands.FSCommand;
import com.mcc.fs.simulator.service.FSCommandsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/fs")
public class FileSystemController {

    private final FSCommandsService fsCommandsService;

    public FileSystemController(FSCommandsService fsCommandsService) {
        this.fsCommandsService = fsCommandsService;
    }

    @GetMapping("commands")
    public List<FSCommand> getCommands() {
        log.info("Returning available commands: {}", fsCommandsService.getCommands());
        return fsCommandsService.getCommands();
    }

}
