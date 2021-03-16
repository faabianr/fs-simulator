package com.mcc.fs.simulator.model;

public class Directory {
    private int inode;
    private String name;

    public Directory(int inode, String name) {
        this.inode = inode;
        this.name = name;
    }

    public int getInode() {
        return inode;
    }

    public void setInode(int inode) {
        this.inode = inode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
