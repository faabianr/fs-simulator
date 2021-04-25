package com.mcc.fs.simulator.model.filesystem;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Slf4j
public abstract class Block {
    public static final int BYTES = 1024;

    protected byte[] content;

    public void writeToDisk() {
        // TODO implement
        log.info("Writing content to disk. Content={}", content);
    }
}
