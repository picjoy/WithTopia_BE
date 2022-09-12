//package com.four.withtopia.config.error;
//
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.ExceptionHandler;
//import org.springframework.web.bind.annotation.RestControllerAdvice;
//
////Global error controller
//@RestControllerAdvice
//public class GlobalExceptionHandler {
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
//
////    //IllegalArgumentException 예외처리
////    @ExceptionHandler({IllegalArgumentException.class})
////    public ResponseDto<?> handleException(IllegalArgumentException ex){
////        return ResponseDto.fail("BAD_REQUEST",ex.getMessage());
////    }
//}
//
