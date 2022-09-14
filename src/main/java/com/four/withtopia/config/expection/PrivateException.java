package com.four.withtopia.config.expection;

import com.four.withtopia.config.error.ErrorCode;
import lombok.Getter;

@Getter
public class PrivateException extends RuntimeException{

    private ErrorCode errorCode;

    public PrivateException(ErrorCode errorCode){
        super(errorCode.getErrormessage());
        this.errorCode = errorCode;
    }
}
