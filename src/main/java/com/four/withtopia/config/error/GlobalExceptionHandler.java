package com.four.withtopia.config.error;

import com.four.withtopia.dto.response.ResponseDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

//Global error controller
@RestControllerAdvice
public class GlobalExceptionHandler {

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
        return ResponseEntity.badRequest().body(ex.getMessage());
    }

    @ExceptionHandler({NullPointerException.class})
    public ResponseEntity<?> handleException(NullPointerException ex){
        return ResponseEntity.status(404).body(ex.getMessage());
    }
}

