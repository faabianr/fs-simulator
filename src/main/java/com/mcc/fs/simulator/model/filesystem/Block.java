package com.mcc.fs.simulator.model.filesystem;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Slf4j
public class Block {
    public static final int BYTES = 1024;

    protected byte[] content;

    public void initEmpty() {
        content = new byte[BYTES];
        Arrays.fill(content, (byte) 0);
    }

    public void writeToDisk() {
        // TODO implement
        log.info("Writing content to disk. Content={}", content);
    }
}
