package com.four.withtopia.config.error;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {
    
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

    //좋아요 예외처리
    
    LIKE_JWT_NOT_FOUND_FAIL(HttpStatus.NOT_FOUND,"109","JWT 토큰이 잘못되었습니다"),

    LIKE_MEMBER_NOT_FOUND_FAIL(HttpStatus.NOT_FOUND,"110","유저가 아닙니다"),

    LIKE_POST_NOT_FOUND_FAIL(HttpStatus.NOT_FOUND,"111","게시글이 존재하지 않습니다."),
    
    //댓글 예외처리
    
    COMMENT_ACCOUNT_NOT_FOUND_ERROR(HttpStatus.NOT_FOUND,"112","로그인을 해주세요!"),

    COMMENT_POST_NOT_FOUND_ERROR(HttpStatus.NOT_FOUND,"113","게시글이 존재하지 않습니다"),

    COMMENT_COMMENT_NOT_FOUND_ERROR(HttpStatus.NOT_FOUND,"114","댓글이 존재하지 않습니다"),
    
    //게시글 예외처리

    POST_POST_NOT_FOUND_ERROR(HttpStatus.NOT_FOUND,"115","게시글이 존재하지 않습니다"),

    POST_ACCOUNT_NOT_FOUND_ERROR(HttpStatus.NOT_FOUND,"116","로그인을 해주세요!"),

    POST_MEMBER_NOT_AUTH_ERROR(HttpStatus.BAD_REQUEST,"117","해당 게시물에 대한 수정 권한이 없습니다."),

    POST_TAG_DUPLICATION_ERROR(HttpStatus.BAD_REQUEST,"118","중복된 태그"),

    POST_MEMEBER_NOT_FOUND_ERROR(HttpStatus.NOT_FOUND,"119","조회된 멤버가 없습니다"),

    OK(HttpStatus.OK, "0", "정상"),

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
    NOT_VOTE_TO_SELF_ERROR(HttpStatus.BAD_REQUEST,"126","자신에게 투표할 수 없습니다.");


    private final HttpStatus httpStatus;
    private final String errorCode;
    private final String errormessage;

    ErrorCode(HttpStatus httpStatus, String errorCode, String errormessage) {
        this.httpStatus = httpStatus;
        this.errorCode = errorCode;
        this.errormessage = errormessage;
    }
}
