package com.four.withtopia.config.error;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class ErrorCode {

    private HttpStatus httpStatus;
    private String errorCode;
    private String errormessage;

    public ErrorCode(HttpStatus httpStatus, String errorCode, String errormessage) {
        this.httpStatus = httpStatus;
        this.errorCode = errorCode;
        this.errormessage = errormessage;
    }
}
