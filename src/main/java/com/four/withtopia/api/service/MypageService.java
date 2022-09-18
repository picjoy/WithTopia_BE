package com.four.withtopia.api.service;

import com.four.withtopia.config.security.jwt.TokenProvider;
import com.four.withtopia.db.domain.Member;
import com.four.withtopia.db.repository.MemberRepository;
import com.four.withtopia.dto.request.ChangePasswordRequestDto;
import com.four.withtopia.dto.request.ProfileUpdateRequestDto;
import com.four.withtopia.dto.response.MypageResponseDto;
import com.four.withtopia.util.MemberCheckUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.time.Instant;
import java.time.ZoneId;
import java.util.*;

@Service
@RequiredArgsConstructor
public class MypageService {
    private final PasswordEncoder passwordEncoder;
    private final MemberRepository memberRepository;
    private final TokenProvider tokenProvider;
    private final MemberCheckUtils memberCheckUtils;

    @Transactional(readOnly = true)
    public ResponseEntity<?> getMypage( HttpServletRequest request){
        // 토큰 검사
        Member member = memberCheckUtils.checkMember(request);
        MypageResponseDto responseDto = MypageResponseDto.createMypageResponseDto(member);
        return ResponseEntity.ok(responseDto);
    }

    @Transactional
    public ResponseEntity<?> updateMemberInfo(ProfileUpdateRequestDto requestDto, HttpServletRequest request){
        // 토큰 검사
        Member member = memberCheckUtils.checkMember(request);

        member.updateMember(requestDto);
        memberRepository.save(member);
        MypageResponseDto responseDto = MypageResponseDto.createMypageResponseDto(member);

        return ResponseEntity.ok(responseDto);
    }

    @Transactional
    public ResponseEntity<?> deleteMember(HttpServletRequest request){
        // 토큰 검사
        Member member = memberCheckUtils.checkMember(request);

        member.deleteMember();
        memberRepository.save(member);

        // 3일 뒤 회원 지우기
        memberDelete();

        return ResponseEntity.ok("success");
    }

    @Transactional
    public ResponseEntity<?> changePassword(ChangePasswordRequestDto requestDto){
        Member member = tokenProvider.getMemberFromAuthentication();
        if (member.validatePassword(passwordEncoder,requestDto.getPassword())){
            return  ResponseEntity.ok("이전 비밀번호를 확인해주세요!");
        }
        if (!Objects.equals(requestDto.getPassword(),requestDto.getPasswordConfirm())){
            return  ResponseEntity.ok("비밀번호를 확인해주세요!");
        }
        String password = passwordEncoder.encode(requestDto.getPassword());
        member.updatePw(password);
        memberRepository.save(member);
        return ResponseEntity.ok("success");
    }

    // 회원이 탈퇴한 후 3일이 지나면 회원 내역 삭제
    public void memberDelete(){
        // 3일 뒤
        long lateTime = 1000 * 60 * 60 * 24 * 3;
//
//        Member deleteMember = memberRepository.findByIsDelete(true);
//        System.out.println("멤버 찾음");

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

        finalDelete.schedule(deleteTask, 60000);
    }
}
