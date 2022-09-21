package com.four.withtopia.config.error;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class ErrorCode {

//    POST_MEMEBER_NOT_FOUND_ERROR(HttpStatus.NOT_FOUND,"119","조회된 멤버가 없습니다"),
//
//    OK(HttpStatus.OK, "0", "정상"),
//
//    // 채팅방
//    NOT_FOUND_ROOM(HttpStatus.BAD_REQUEST, "120", "채팅방을 찾을 수 없습니다."),
//    MEMBER_NOT_AUTH_ERROR_ROOM(HttpStatus.BAD_REQUEST,"121","해당 방에 대한 삭제 권한이 없습니다."),
//    ACCOUNT_NOT_FOUND_ERROR(HttpStatus.NOT_FOUND,"122","로그인을 해주세요!"),
//    ROOM_IS_FULL(HttpStatus.BAD_REQUEST, "123", "방에 인원이 가득 찼습니다."),
//    NOT_FOUND_ROOM_MEMBER(HttpStatus.BAD_REQUEST, "124", "채팅방을 찾을 수 없습니다."),
//    ALREADY_IN_ROOM_MEMBER(HttpStatus.BAD_REQUEST, "125", "이미 들어간 멤버입니다."),
//
//
//
//    // 투표
//    VOTE_DUPLICATION_ERROR(HttpStatus.BAD_REQUEST,"124","이미 투표를 완료했습니다."),
//    HAVE_NOT_POPULARITY_ERROR(HttpStatus.BAD_REQUEST, "125", "더이상 내려갈 인기도가 없습니다."),
//    NOT_VOTE_TO_SELF_ERROR(HttpStatus.BAD_REQUEST,"126","자신에게 투표할 수 없습니다."),
//    WRONG_EMAIL(HttpStatus.BAD_REQUEST,"400","잘못된 이메일입니다.");


    private HttpStatus httpStatus;
    private String errorCode;
    private String errormessage;

    public ErrorCode(HttpStatus httpStatus, String errorCode, String errormessage) {
        this.httpStatus = httpStatus;
        this.errorCode = errorCode;
        this.errormessage = errormessage;
    }
}
