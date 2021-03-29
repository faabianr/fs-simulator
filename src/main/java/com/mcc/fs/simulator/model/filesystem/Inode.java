package com.mcc.fs.simulator.model.filesystem;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class Inode {
    private int size;
    private String type; // Tipos: D Directory, - (archivo normal), C - Caracter, P - Pipe, L - link, B - bloque
    private String owner;
    private Date creationDate;
    private String permissions;
    private int[] tableOfContents;
}
