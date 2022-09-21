package com.four.withtopia.config.expection;

import com.four.withtopia.config.error.ErrorCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PrivateException extends RuntimeException{

    private ErrorCode errorCode;
    private String Message;


    public PrivateException(ErrorCode errorCode){
        super(errorCode.getErrormessage());
        this.errorCode = errorCode;
        this.Message = errorCode.getErrormessage();
    }
    public PrivateException(ErrorCode errorCode,String errormessage){
        super(errormessage);
        this.errorCode = errorCode;
        this.Message = errormessage;
    }

}
