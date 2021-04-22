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
//@Data
@Slf4j
public class SuperBlock extends Disk{

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
        byte startInode = 9;
        for (byte i = 0; i < 120; i++) {
            if((i % 4) == 0){
                LBL[i] = startInode;
                startInode++;
            }
        }
        writeToDisk(LBL, 1);
    }

    private void initLIL() {
        log.info("Initializing LIL ...");
        LIL = new byte[1024];
        Arrays.fill(LIL, (byte) 0);
        byte startInode = 3;
        for (byte i = 0; i < 120; i++) {
            if((i % 4) == 0){
                LIL[i] = startInode;
                startInode++;
            }
        }
        writeToDisk(LIL, 2);
    }

}
