package com.four.withtopia.util;

import com.four.withtopia.config.security.jwt.TokenProvider;
import com.four.withtopia.db.domain.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

@Component
@RequiredArgsConstructor
public class MemberCheckUtils {

    private final TokenProvider tokenProvider;

    public Member checkMember(HttpServletRequest request){

        if(request.getSession().getAttribute("RefreshToken") == null){
            throw new NullPointerException("로그인을 해주세요.");
        }

        if (!tokenProvider.validateToken(request.getSession().getAttribute("RefreshToken").toString())) {
            throw new IllegalArgumentException("Token이 유효하지 않습니다.");
        }

        Member member = tokenProvider.getMemberFromAuthentication();

        if (null == member) {
            throw new NullPointerException("사용자를 찾을 수 없습니다.");
        }
        return member;
    }
}
