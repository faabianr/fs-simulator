package com.mcc.fs.simulator.model;

import java.util.Date;

public class Inode {
    private int size;
    private String type; // Tipos: D Directory, - (archivo normal), C - Caracter, P - Pipe, L - link, B - bloque
    // podriamos ponerlo como char.
    private String owner;
    private Date creationDate;
    private String permissions;
    private int tableOfContents[] = new int[11];

    public Inode(int size, String type, String owner, Date creationDate, String permissions, int[] tableOfContents) {
        this.size = size;
        this.type = type;
        this.owner = owner;
        this.creationDate = creationDate;
        this.permissions = permissions;
        this.tableOfContents = tableOfContents;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public String getPermissions() {
        return permissions;
    }

    public void setPermissions(String permissions) {
        this.permissions = permissions;
    }

    public int[] getTableOfContents() {
        return tableOfContents;
    }

    public void setTableOfContents(int[] tableOfContents) {
        this.tableOfContents = tableOfContents;
    }
}
