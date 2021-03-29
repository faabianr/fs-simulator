package com.mcc.fs.simulator.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/fs")
public class FileSystemController {

    @GetMapping("commands")
    public String getCommands() {
        return "sample"; // TODO: implement properly
    }

}
