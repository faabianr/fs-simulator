package com.mcc.fs.simulator.model.filesystem;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@AllArgsConstructor
//@NoArgsConstructor
//@Data
@Slf4j
public abstract class Block extends Disk {
    public static final int SIZE = 1024;

    public void writeToDisk() {
        // TODO implement
        //log.info("Writing content to disk. Content=\n{}", content);
    }
}
