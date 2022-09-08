package com.four.withtopia.util;

import com.four.withtopia.config.security.jwt.TokenProvider;
import com.four.withtopia.db.domain.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

@Component
@RequiredArgsConstructor
public class MemberCheckUtils {

    private final TokenProvider tokenProvider;
    private final PasswordEncoder passwordEncoder;

    public Member member(){
        Member member = tokenProvider.getMemberFromAuthentication();

        return member;
    }

    public ResponseEntity<?> checkMember(HttpServletRequest request){

        Member member = member();

        if(request.getSession().getAttribute("RefreshToken") == null){
            return ResponseEntity.badRequest().body("로그인을 해주세요.");
        }

        if (!tokenProvider.validateToken(request.getSession().getAttribute("RefreshToken").toString())) {
            return ResponseEntity.badRequest().body("Token이 유효하지 않습니다.");
        }

        if (null == member) {
            return ResponseEntity.badRequest().body("사용자를 찾을 수 없습니다.");
        }
        return null;
    }
}
