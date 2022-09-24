package com.four.withtopia.util;

import com.four.withtopia.config.error.ErrorCode;
import com.four.withtopia.config.expection.PrivateException;
import com.four.withtopia.config.security.jwt.TokenProvider;
import com.four.withtopia.db.domain.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

@Component
@RequiredArgsConstructor
public class MemberCheckUtils {

    private final TokenProvider tokenProvider;

    public Member checkMember(HttpServletRequest request){

        if(request.getHeader("RefreshToken") == null){
            throw new PrivateException(new ErrorCode(HttpStatus.OK,"200","로그인을 해주세요."));
        }

        if (!tokenProvider.validateToken(request.getHeader("RefreshToken"))) {
            throw new PrivateException(new ErrorCode(HttpStatus.OK,"200","Token이 유효하지 않습니다."));
        }

        Member member = tokenProvider.getMemberFromAuthentication();

        if (null == member) {
            throw new PrivateException(new ErrorCode(HttpStatus.OK,"200","사용자를 찾을 수 없습니다."));
        }

        // 멤버가 탈퇴한 회원인 경우
        if (member.isDelete()){
            throw new PrivateException(new ErrorCode(HttpStatus.OK,"200","탈퇴한지 3일이 경과하지 않았습니다."));
        }

        return member;
    }
}
