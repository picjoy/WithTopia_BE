package com.four.withtopia.config.expection;

import com.four.withtopia.config.error.ErrorCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PrivateResponseBody {
    private String statusCode;
    private String statusMsg;
    private Object data;

    public PrivateResponseBody(ErrorCode errorCode) {
        this.statusCode = errorCode.getErrorCode();
        this.statusMsg = errorCode.getErrormessage();

    }

    public PrivateResponseBody(ErrorCode errorCode, Object data){
        this.statusCode = errorCode.getErrorCode();
        this.statusMsg = errorCode.getErrormessage();
        this.data = data;
    }


    public PrivateResponseBody(Exception e) {
        this.statusMsg = e.getMessage();
    }

}
