package com.four.withtopia.config.error;

import com.four.withtopia.config.expection.PrivateException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

//Global error controller
@RestControllerAdvice
public class GlobalExceptionHandler {
//
//    //커스텀 예외처리
//    @ExceptionHandler(value = { CustomException.class })
//    public ResponseEntity<Object> handleApiRequestException(CustomException ex) {
//        String errorCode = ex.getErrorCode().getErrorCode();
//        String errorMessage = ex.getErrorCode().getErroemessage();
//        ErrorResponseBody errorResponseBody = new ErrorResponseBody();
//        errorResponseBody.setErrorCode(errorCode);
//        errorResponseBody.setErrorMessage(errorMessage);
//
//        return new ResponseEntity(
//                errorResponseBody,
//                ex.getErrorCode().getHttpStatus()
//        );
//    }

    //IllegalArgumentException 예외처리
    @ExceptionHandler({IllegalArgumentException.class})
    public ResponseEntity<?> handleException(IllegalArgumentException ex){
        return ResponseEntity.badRequest().header("Content-Type","application/json; charset=UTF-8").body(ex.getMessage());
    }
    @ExceptionHandler({PrivateException.class})
    public ResponseEntity<?> handleException(PrivateException ex){
        return ResponseEntity.status(ex.getErrorCode().getHttpStatus()).header("Content-Type","application/json; charset=UTF-8").body(ex.getErrorCode());
    }

//    NullPointerException 예외처리
    @ExceptionHandler({NullPointerException.class})
    public ResponseEntity<?> handleException(NullPointerException ex){
        return ResponseEntity.status(404).header("Content-Type","application/json; charset=UTF-8").body(ex);
    }
}

