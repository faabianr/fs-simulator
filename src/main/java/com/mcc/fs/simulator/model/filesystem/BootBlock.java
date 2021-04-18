package com.mcc.fs.simulator.model.filesystem;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@Slf4j
public class BootBlock {
    public static final int SIZE = 1024;

    private byte[] content;

    public void init() {
        log.info("Initializing boot ...");
        content = new byte[1024];
        Arrays.fill(content, (byte) 0);
    }

}
