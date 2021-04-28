package com.mcc.fs.simulator.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.INTERNAL_SERVER_ERROR, reason = "There are no free inodes")
public class NoFreeInodesException extends RuntimeException {

}
