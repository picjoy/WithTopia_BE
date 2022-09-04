package com.four.withtopia.api.service;

import com.four.withtopia.config.security.UserDetailsImpl;
import com.four.withtopia.config.security.jwt.TokenProvider;
import com.four.withtopia.db.domain.Member;
import com.four.withtopia.db.repository.MemberRepository;
import com.four.withtopia.dto.request.ProfileUpdateRequestDto;
import com.four.withtopia.dto.response.MypageResponseDto;
import com.four.withtopia.dto.response.ResponseDto;
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

    @Transactional(readOnly = true)
    public ResponseEntity<?> getMypage( HttpServletRequest request){
        if (!tokenProvider.validateToken(request.getHeader("Refresh-Token"))) {
            return ResponseEntity.badRequest().body("Token이 유효하지 않습니다.");
//                    "INVALID_TOKEN", "Token이 유효하지 않습니다.");
        }
        Member member = tokenProvider.getMemberFromAuthentication();
        if (null == member) {
            return ResponseEntity.badRequest().body("사용자를 찾을 수 없습니다.");
//            return ResponseDto.fail("MEMBER_NOT_FOUND", "사용자를 찾을 수 없습니다.");
        }
        MypageResponseDto responseDto = MypageResponseDto.createMypageResponseDto(member);
        return ResponseEntity.ok(responseDto);
    }

    @Transactional
    public ResponseEntity<?> updateMemberInfo(ProfileUpdateRequestDto requestDto, HttpServletRequest request){
        if (!tokenProvider.validateToken(request.getHeader("Refresh-Token"))) {
            return ResponseEntity.badRequest().body("Token이 유효하지 않습니다.");
//                    "INVALID_TOKEN", "Token이 유효하지 않습니다.");
        }
        Member member = tokenProvider.getMemberFromAuthentication();
        if (null == member) {
            return ResponseEntity.badRequest().body("사용자를 찾을 수 없습니다.");
//            return ResponseDto.fail("MEMBER_NOT_FOUND", "사용자를 찾을 수 없습니다.");
        }

        Member updateMember = member.updateMember(requestDto);
        memberRepository.save(updateMember);

        MypageResponseDto responseDto = MypageResponseDto.createMypageResponseDto(updateMember);

        return ResponseEntity.ok(responseDto);
    }

    @Transactional
    public ResponseEntity<?> deleteMember(HttpServletRequest request){
        if (!tokenProvider.validateToken(request.getHeader("Refresh-Token"))) {
            return ResponseEntity.badRequest().body("Token이 유효하지 않습니다.");
//                    "INVALID_TOKEN", "Token이 유효하지 않습니다.");
        }
        Member member = tokenProvider.getMemberFromAuthentication();
        if (null == member) {
            return ResponseEntity.badRequest().body("사용자를 찾을 수 없습니다.");
//            return ResponseDto.fail("MEMBER_NOT_FOUND", "사용자를 찾을 수 없습니다.");
        }

        Member deleteMember = member.deleteMember();
        memberRepository.save(deleteMember);

        return ResponseEntity.ok("success");
    }
}
