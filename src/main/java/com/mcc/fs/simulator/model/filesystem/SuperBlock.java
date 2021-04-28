package com.mcc.fs.simulator.model.filesystem;

import com.mcc.fs.simulator.exception.NoFreeBlocksException;
import com.mcc.fs.simulator.exception.NoFreeInodesException;
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
    private Queue<Byte> LILqueue;
    private Queue<Byte> LBLqueue;
    private byte peekLIL;
    private byte peekLBL;

    public void init() {
        log.info("Init superblock ...");
        LILqueue = new LinkedList<>();
        LBLqueue = new LinkedList<>();
        initLBL();
        initLIL();
    }

    public byte getNextFreeInode() throws NoFreeInodesException {
        if (LILqueue.isEmpty()) {
            log.error("the LIL is empty");
            throw new NoFreeInodesException();
        }
        return LILqueue.poll();
    }

    public byte getNextFreeBlock() throws NoFreeBlocksException {
        if (LILqueue.isEmpty()) {
            log.error("the LBL is empty");
            throw new NoFreeBlocksException();
        }
        return LBLqueue.poll();
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
