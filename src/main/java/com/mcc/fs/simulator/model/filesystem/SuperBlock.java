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
public class SuperBlock {

    public static final int SIZE = 2048;

    private byte[] LBL; // 256 bloques en 1k
    private byte[] LIL; // 16 numeros de inodos libres

    public void init() {
        log.info("Init superblock ...");
        initLBL();
        initLIL();
    }

    private void initLBL() {
        log.info("Initializing LBL ...");
        LBL = new byte[1024];
        Arrays.fill(LBL, (byte) 0);
        LBL[0] = 9;
        LBL[1] = 10;
        LBL[2] = 11;
    }

    private void initLIL() {
        log.info("Initializing LIL ...");
        LIL = new byte[1024];
        Arrays.fill(LIL, (byte) 0);
        byte startInode = 3;
        for (byte i = 0; i <= 16; i++) {
            LIL[i] = startInode;
            startInode++;
        }
        LIL[0] = 3;
        LIL[1] = 4;
        LIL[2] = 5;
    }

}
