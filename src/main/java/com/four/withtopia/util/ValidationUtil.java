package com.four.withtopia.util;

import com.four.withtopia.db.domain.EmailAuth;
import com.four.withtopia.db.repository.EmailAuthRepository;
import com.four.withtopia.db.repository.MemberRepository;
import com.four.withtopia.dto.request.MemberRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Objects;

@RequiredArgsConstructor
@Component
public class ValidationUtil {

    private final MemberRepository memberRepository;
    private final EmailAuthRepository emailAuthRepository;


    //    아이디 인증 여부 체크 (회원가입) (True면 존재함)
    public boolean emailExist(String email){
       return memberRepository.existsByEmail(email);
    }

    public boolean nicknameExist(String nickname){
        return memberRepository.existsByNickName(nickname);
    }

    //    아이디 인증 여부 체크 (회원가입) (True면 인증안됨)
    public boolean emailAuth(MemberRequestDto requestDto){
        EmailAuth emailAuth = emailAuthRepository.findByEmail(requestDto.getEmail());
        if (emailAuth == null){
            return true;
        }
        return !Objects.equals(emailAuth.getAuth(), requestDto.getAuthKey());
    }

    public boolean emailexist(MemberRequestDto requestDto){
        return emailAuthRepository.existsByEmail(requestDto.getEmail());
    }


//    비밀번호 = 비밀번호 확인 체크 (회원가입)
    public boolean passwordCheck(MemberRequestDto requestDto){
        return Objects.equals(requestDto.getPassword(), requestDto.getPasswordConfirm());
    }




}
