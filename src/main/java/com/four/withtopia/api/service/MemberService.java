package com.four.withtopia.api.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.four.withtopia.config.security.jwt.TokenProvider;
import com.four.withtopia.db.domain.Member;
import com.four.withtopia.db.repository.MemberRepository;
import com.four.withtopia.dto.KakaoUserInfoDto;
import com.four.withtopia.dto.request.LoginRequestDto;
import com.four.withtopia.dto.request.TokenDto;
import com.four.withtopia.dto.response.MemberResponseDto;
import com.four.withtopia.dto.response.ResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class MemberService {

  private final MemberRepository memberRepository;

  private final PasswordEncoder passwordEncoder;
//  private final AuthenticationManagerBuilder authenticationManagerBuilder;
  private final TokenProvider tokenProvider;
  private final KakaoService kakaoService;



  @Transactional
  public ResponseDto<?> login(LoginRequestDto requestDto, HttpServletResponse response) {
    Member member = isPresentMember(requestDto.getNickname());
    if (null == member) {
      return ResponseDto.fail("MEMBER_NOT_FOUND",
          "사용자를 찾을 수 없습니다.");
    }

    if (!member.validatePassword(passwordEncoder, requestDto.getPassword())) {
      return ResponseDto.fail("INVALID_MEMBER", "사용자를 찾을 수 없습니다.");
    }

//    UsernamePasswordAuthenticationToken authenticationToken =
//        new UsernamePasswordAuthenticationToken(requestDto.getNickname(), requestDto.getPassword());
//    Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);

    TokenDto tokenDto = tokenProvider.generateTokenDto(member);
    tokenToHeaders(tokenDto, response);

    return ResponseDto.success(
        MemberResponseDto.builder()
            .id(member.getMemberId())
            .nickname(member.getNickName())
            .createdAt(member.getCreatedAt())
            .modifiedAt(member.getModifiedAt())
            .build()
    );
  }

//  @Transactional
//  public ResponseDto<?> reissue(HttpServletRequest request, HttpServletResponse response) {
//    if (!tokenProvider.validateToken(request.getHeader("Refresh-Token"))) {
//      return ResponseDto.fail("INVALID_TOKEN", "Token이 유효하지 않습니다.");
//    }
//    Member member = tokenProvider.getMemberFromAuthentication();
//    if (null == member) {
//      return ResponseDto.fail("MEMBER_NOT_FOUND",
//          "사용자를 찾을 수 없습니다.");
//    }
//
//    Authentication authentication = tokenProvider.getAuthentication(request.getHeader("Access-Token"));
//    RefreshToken refreshToken = tokenProvider.isPresentRefreshToken(member);
//
//    if (!refreshToken.getValue().equals(request.getHeader("Refresh-Token"))) {
//      return ResponseDto.fail("INVALID_TOKEN", "Token이 유효하지 않습니다.");
//    }
//
//    TokenDto tokenDto = tokenProvider.generateTokenDto(member);
//    refreshToken.updateValue(tokenDto.getRefreshToken());
//    tokenToHeaders(tokenDto, response);
//    return ResponseDto.success("success");
//  }

  public ResponseDto<?> logout(HttpServletRequest request) {
    if (!tokenProvider.validateToken(request.getHeader("Refresh-Token"))) {
      return ResponseDto.fail("INVALID_TOKEN", "Token이 유효하지 않습니다.");
    }
    Member member = tokenProvider.getMemberFromAuthentication();
    if (null == member) {
      return ResponseDto.fail("MEMBER_NOT_FOUND",
          "사용자를 찾을 수 없습니다.");
    }

    return tokenProvider.deleteRefreshToken(member);
  }

  public ResponseEntity<?> kakaoLogin(String code, HttpServletResponse response) throws JsonProcessingException {
    // 인가코드 받아서 카카오 엑세스 토큰 받기
    String kakaoAccessToken = kakaoService.getKakaoAccessToken(code);
    // 카카오 엑세스 토큰으로 유저 정보 받아오기
    KakaoUserInfoDto kakaoUserInfo = kakaoService.getKakaoUserInfo(kakaoAccessToken);
    // 회원가입 필요 시 회원 가입
    Member createMember = kakaoService.createKakaoMember(kakaoUserInfo);
    // 로그인 - 토큰 헤더에 넣어주기
    socialLogin(createMember, response);
    // MemberResponseDto
    MemberResponseDto responseDto = MemberResponseDto.createSocialMemberResponseDto(createMember);

    return ResponseEntity.ok(responseDto);
  }

//  public ResponseEntity<?> googleLogin(String code, HttpServletResponse response) throws JsonProcessingException {
//    // 인가코드 받아서 구글 엑세스 토큰 받기
//    String googleAccessToken =
//    return ResponseEntity.ok(responseDto);
//  }

  @Transactional(readOnly = true)
  public Member isPresentMember(String nickname) {
    Optional<Member> optionalMember = memberRepository.findByNickName(nickname);
    return optionalMember.orElse(null);
  }

  public void tokenToHeaders(TokenDto tokenDto, HttpServletResponse response) {
    response.addHeader("Authorization", "Bearer " + tokenDto.getAccessToken());
    response.addHeader("Refresh-Token", tokenDto.getRefreshToken());
    response.addHeader("Access-Token-Expire-Time", tokenDto.getAccessTokenExpiresIn().toString());
  }

  // 소셜 로그인 - 토큰 만들어서 헤더에 넣어주기
  public void socialLogin(Member socialUser, HttpServletResponse response){
    TokenDto tokenDto = tokenProvider.generateTokenDto(socialUser);
    tokenToHeaders(tokenDto, response);
  }

  public ResponseEntity<?> createMember(MemberRequestDto requestDto) {
    if (requestDto.getAuthKey() == null) {
      ResponseEntity.ok("이메일 인증번호를 적어주세요!");
    }
    Member member = new Member(requestDto);
    memberRepository.save(member);
    return ResponseEntity.ok("success");
  }
}
