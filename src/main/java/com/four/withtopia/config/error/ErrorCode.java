package com.four.withtopia.config.error;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {

    // 정상 처리
    OK(HttpStatus.OK, "0", "정상"),
    
    //회원가입 예외처리
    SIGNUP_ACCOUNT_NOT_FOUND_ERROR(HttpStatus.BAD_REQUEST,"101","아이디를 작성해주세요"),
    SIGNUP_USERNAME_NOT_FOUND_ERROR(HttpStatus.BAD_REQUEST,"102","닉네임을 작성해주세요!"),
    SIGNUP_ACCOUNT_DUPLICATE_ERROR(HttpStatus.BAD_REQUEST,"103","아이디가 중복됩니다"),
    SIGNUP_PASSWORD_CHECK_ERROR(HttpStatus.BAD_REQUEST,"104","비밀번호와 비밀번호확인이 다릅니다"),
    SIGNUP_USERCOMMENT_NOT_FOUND_ERROR(HttpStatus.BAD_REQUEST,"105","한줄평을 적어주세요"),
    SIGNUP_USERIMAGE_NOT_FOUND_ERROR(HttpStatus.BAD_REQUEST,"106","프로필이미지가 없습니다"),

    //로그인 예외처리
    
    LOGIN_ACCOUNT_NOT_FOUND_FAIL(HttpStatus.NOT_FOUND,"107","아이디가 없습니다"),

    LOGIN_PASSWORD_NOT_FOUND_FAIL(HttpStatus.NOT_FOUND,"108","비밀번호가 일치하지 않습니다."),

    POST_MEMEBER_NOT_FOUND_ERROR(HttpStatus.NOT_FOUND,"119","조회된 멤버가 없습니다"),

    // 채팅방
    NOT_FOUND_ROOM(HttpStatus.BAD_REQUEST, "120", "채팅방을 찾을 수 없습니다."),
    MEMBER_NOT_AUTH_ERROR_ROOM(HttpStatus.BAD_REQUEST,"121","해당 방에 대한 삭제 권한이 없습니다."),
    ACCOUNT_NOT_FOUND_ERROR(HttpStatus.NOT_FOUND,"122","로그인을 해주세요!"),
    ROOM_IS_FULL(HttpStatus.BAD_REQUEST, "123", "방에 인원이 가득 찼습니다."),
    NOT_FOUND_ROOM_MEMBER(HttpStatus.BAD_REQUEST, "124", "채팅방을 찾을 수 없습니다."),
    ALREADY_IN_ROOM_MEMBER(HttpStatus.BAD_REQUEST, "125", "이미 들어간 멤버입니다."),

    // 투표
    VOTE_DUPLICATION_ERROR(HttpStatus.BAD_REQUEST,"124","이미 투표를 완료했습니다."),
    HAVE_NOT_POPULARITY_ERROR(HttpStatus.BAD_REQUEST, "125", "더이상 내려갈 인기도가 없습니다."),
    NOT_VOTE_TO_SELF_ERROR(HttpStatus.BAD_REQUEST,"126","자신에게 투표할 수 없습니다."),

    // 멤버 조회
    MEMBER_NOT_FOUND_FAIL(HttpStatus.NOT_FOUND,"110","유저가 아닙니다."),

    // 친구
    CAN_NOT_FRIEND_YOURSELF(HttpStatus.NOT_FOUND, "111","자신과는 친구가 될 수 없습니다."),
    NOT_FOUND_MY_FRIEND(HttpStatus.BAD_REQUEST,"112", "친구 리스트에서 해당 친구를 찾을 수 없습니다."),
    ALREADY_IN_MY_FRIEND(HttpStatus.BAD_REQUEST, "123", "이미 친구입니다.");


    private final HttpStatus httpStatus;
    private final String errorCode;
    private final String errormessage;

    ErrorCode(HttpStatus httpStatus, String errorCode, String errormessage) {
        this.httpStatus = httpStatus;
        this.errorCode = errorCode;
        this.errormessage = errormessage;
    }
}
