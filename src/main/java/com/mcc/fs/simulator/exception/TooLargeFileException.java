package com.mcc.fs.simulator.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_REQUEST, reason = "File is too large to be stored")
public class TooLargeFileException extends RuntimeException {

}
