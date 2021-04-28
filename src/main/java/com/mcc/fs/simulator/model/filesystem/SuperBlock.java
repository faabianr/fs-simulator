package com.mcc.fs.simulator.model.filesystem;

import com.sun.javafx.image.impl.ByteRgb;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@Slf4j
public class SuperBlock {

    public static final int BYTES = 2048;

    private byte[] LBL; // 256 bloques en 1k
    private byte[] LIL; // 16 numeros de inodos libres
    private Queue<Byte> LILqueue = new LinkedList<Byte>();
    private Queue<Byte> LBLqueue = new LinkedList<Byte>();
    private byte peekLIL;
    private byte peekLBL;

    public void init() {
        log.info("Init superblock ...");
        initLBL();
        initLIL();
    }

    private void initLBL() {
        log.info("Initializing LBL ...");
        
        LBL = new byte[Block.BYTES];
        Arrays.fill(LBL, (byte) 0);
        byte startBlock = 9;
        for (byte i = 0; i < 100; i++) {
            LBL[i] = startBlock;
            LBLqueue.add(LBL[i]);
            startBlock++;
        }
       
    }

    private void initLIL() {
        log.info("Initializing LIL ...");
        LIL = new byte[Block.BYTES];
        Arrays.fill(LIL, (byte) 0);
        byte startInode = 3;
        for (byte i = 0; i < 16; i++) {
            LIL[i] = startInode;
            LILqueue.add(LIL[i]);
           startInode++;
       }
       
    }

}
