package com.mcc.fs.simulator.model.filesystem;

import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;

@Slf4j
public class BootBlock extends Disk {
    
    public void init() {
        log.info("Initializing boot ...");
        content= new byte[1024];
        
        writeToDisk(content, 0);
    }

}
