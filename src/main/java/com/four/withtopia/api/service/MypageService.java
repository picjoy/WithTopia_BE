package com.four.withtopia.api.service;

import com.four.withtopia.config.security.UserDetailsImpl;
import com.four.withtopia.config.security.jwt.TokenProvider;
import com.four.withtopia.db.domain.Member;
import com.four.withtopia.db.repository.MemberRepository;
import com.four.withtopia.dto.request.ProfileUpdateRequestDto;
import com.four.withtopia.dto.response.MypageResponseDto;
import com.four.withtopia.dto.response.ResponseDto;
import com.four.withtopia.util.MemberCheckUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;

@Service
@RequiredArgsConstructor
public class MypageService {
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

        return ResponseEntity.ok("success");
    }
}
