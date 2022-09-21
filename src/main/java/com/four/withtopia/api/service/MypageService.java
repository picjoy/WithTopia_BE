package com.four.withtopia.api.service;

import com.four.withtopia.config.error.ErrorCode;
import com.four.withtopia.config.expection.PrivateException;
import com.four.withtopia.config.security.jwt.TokenProvider;
import com.four.withtopia.db.domain.Member;
import com.four.withtopia.db.repository.MemberRepository;
import com.four.withtopia.dto.request.ChangePasswordRequestDto;
import com.four.withtopia.dto.request.ProfileUpdateRequestDto;
import com.four.withtopia.dto.response.MypageResponseDto;
import com.four.withtopia.util.MemberCheckUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

@Service
@RequiredArgsConstructor
public class MypageService {
    private final PasswordEncoder passwordEncoder;
    private final MemberRepository memberRepository;
    private final TokenProvider tokenProvider;
    private final MemberCheckUtils memberCheckUtils;

    @Transactional(readOnly = true)
    public MypageResponseDto getMypage(HttpServletRequest request){
        // 토큰 검사
        Member member = memberCheckUtils.checkMember(request);
        MypageResponseDto responseDto = MypageResponseDto.createMypageResponseDto(member);
        return responseDto;
    }

    @Transactional
    public MypageResponseDto updateMemberInfo(ProfileUpdateRequestDto requestDto, HttpServletRequest request){
        // 토큰 검사
        Member member = memberCheckUtils.checkMember(request);

        member.updateMember(requestDto);
        memberRepository.save(member);
        MypageResponseDto responseDto = MypageResponseDto.createMypageResponseDto(member);

        return responseDto;
    }

    @Transactional
    public String deleteMember(HttpServletRequest request){
        // 토큰 검사
        Member member = memberCheckUtils.checkMember(request);

        member.deleteMember();
        memberRepository.save(member);

        // 3일 뒤 회원 지우기
        memberDelete();

        return "success";
    }

    @Transactional
    public String changePassword(ChangePasswordRequestDto requestDto){
        Member member = tokenProvider.getMemberFromAuthentication();
        if (member.validatePassword(passwordEncoder,requestDto.getPassword())){
            throw new PrivateException(new ErrorCode(HttpStatus.BAD_REQUEST,"400","현재 비밀번호가 일치하지않습니다."));
        }
        if (!Objects.equals(requestDto.getPassword(),requestDto.getPasswordConfirm())){
            throw new PrivateException(new ErrorCode(HttpStatus.BAD_REQUEST,"400","패스워드가 일치하지않습니다."));
        }
        String password = passwordEncoder.encode(requestDto.getPassword());
        member.updatePw(password);
        memberRepository.save(member);
        return "success";
    }

    // 회원이 탈퇴한 후 3일이 지나면 회원 내역 삭제
    public void memberDelete(){
        // 3일 뒤
        long lateTime = 1000 * 60 * 60 * 24 * 3;

        Timer finalDelete = new Timer();
        TimerTask deleteTask = new TimerTask() {
            @Override
            public void run() {
                // 멤버 지우기
                Member deleteMember = memberRepository.findByIsDelete(true);
                memberRepository.delete(deleteMember);
                finalDelete.cancel();
            }
        };

        finalDelete.schedule(deleteTask, lateTime);
    }
}
