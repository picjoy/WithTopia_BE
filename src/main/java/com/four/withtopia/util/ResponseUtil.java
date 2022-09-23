package com.four.withtopia.util;


import com.four.withtopia.config.error.ErrorCode;
import com.four.withtopia.config.expection.PrivateResponseBody;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class ResponseUtil<T> {


    public ResponseEntity<?> forSuccess(T data){
        return new ResponseEntity<>(
                new PrivateResponseBody(
                        new ErrorCode(HttpStatus.OK, "200", "정상") , data
                ) ,
                HttpStatus.OK);
    }

}
