package com.mcc.fs.simulator.model.filesystem;

import lombok.Builder;
import lombok.Data;

import java.util.Date;

@Builder
@Data
public class Inode {
    private int size;
    private FileType type; // Tipos: D Directory, - (archivo normal), C - Caracter, P - Pipe, L - link, B - bloque
    private String owner;
    private Date creationDate;
    private String permissions;
    private int[] tableOfContents;
}
