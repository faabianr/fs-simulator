package com.mcc.fs.simulator.config;

import com.mcc.fs.simulator.model.filesystem.FilePermission;

public class Constants {

    public static final String ROOT_USER = "root";
    public static final int ROOT_DIRECTORY_INODE = 2;

    public static final String DEFAULT_OWNER = ROOT_USER;
    public static final FilePermission DEFAULT_PERMISSIONS = FilePermission.RESTRICTED_TO_OWNER;
}
