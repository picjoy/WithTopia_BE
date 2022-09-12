package com.four.withtopia.api.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.four.withtopia.config.security.jwt.TokenProvider;
import com.four.withtopia.db.domain.Member;
import com.four.withtopia.db.repository.MemberRepository;
import com.four.withtopia.dto.request.GoogleUserInfoDto;
import com.four.withtopia.dto.request.KakaoUserInfoDto;
import com.four.withtopia.dto.request.LoginRequestDto;
import com.four.withtopia.dto.request.MemberRequestDto;
import com.four.withtopia.dto.response.MemberResponseDto;
import com.four.withtopia.util.MemberCheckUtils;
import com.four.withtopia.util.ValidationUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class MemberService {

  private final MemberRepository memberRepository;
  private final PasswordEncoder passwordEncoder;
  private final TokenProvider tokenProvider;
  private final KakaoService kakaoService;
  private final GoogleService googleService;

  private final ValidationUtil validationUtil;

  private final MemberCheckUtils memberCheckUtils;

  @Transactional
  public ResponseEntity<?> login(LoginRequestDto requestDto, HttpSession session) {
    Member member = isPresentMember(requestDto.getEmail());
    if (null == member) {
      return ResponseEntity.ok("MEMBER_NOT_FOUND 사용자를 찾을 수 없습니다.");
    }

    if (!member.validatePassword(passwordEncoder, requestDto.getPassword())) {
      return ResponseEntity.ok("INVALID_MEMBER 사용자를 찾을 수 없습니다.");
    }

    session.setAttribute("Authorization","Bearer " + tokenProvider.GenerateAccessToken(member));
    session.setAttribute("RefreshToken",tokenProvider.GenerateRefreshToken(member));

    return ResponseEntity.ok(
        MemberResponseDto.builder()
            .id(member.getMemberId())
            .nickname(member.getNickName())
            .email(member.getEmail())
            .ProfileImage(member.getProfileImage())
            .build()
    );
  }


  public ResponseEntity<?> logout(HttpServletRequest request) {
    // 토큰 검사
    Member member = memberCheckUtils.checkMember(request);

    request.getSession().invalidate();
    return tokenProvider.deleteRefreshToken(member);
  }

  
//  카카오 로그인
  public ResponseEntity<?> kakaoLogin(String code, HttpSession session) throws JsonProcessingException {
    // 인가코드 받아서 카카오 엑세스 토큰 받기
    String kakaoAccessToken = kakaoService.getKakaoAccessToken(code);
    // 카카오 엑세스 토큰으로 유저 정보 받아오기
    KakaoUserInfoDto kakaoUserInfo = kakaoService.getKakaoUserInfo(kakaoAccessToken);
    // 회원가입 필요 시 회원 가입
    Member createMember = kakaoService.createKakaoMember(kakaoUserInfo);
    // 로그인 - 토큰 헤더에 넣어주기
    socialLogin(createMember, session);
    // MemberResponseDto
    MemberResponseDto responseDto = MemberResponseDto.createSocialMemberResponseDto(createMember);

    return ResponseEntity.ok(responseDto);
  }

//  구글 로그인
  public ResponseEntity<?> googleLogin(String code, HttpSession session) throws JsonProcessingException {
    // 인가코드 받아서 구글 엑세스 토큰 받기
    String googleAccessToken = googleService.getGoogleAccessToken(code);
    // 구글 엑세스 토큰으로 유저 정보 받아오기
    GoogleUserInfoDto googleUserInfo = googleService.getGoogleUserInfo(googleAccessToken);
    // 회원가입 필요시 회원가입
    Member createMember = googleService.createGoogleMember(googleUserInfo);
    // 로그인 - 토큰 헤더에 넣어주기
    socialLogin(createMember, session);
    // MemberResponseDto
    MemberResponseDto responseDto = MemberResponseDto.createSocialMemberResponseDto(createMember);

    return ResponseEntity.ok(responseDto);
  }

  @Transactional(readOnly = true)
  public Member isPresentMember(String email) {
    Optional<Member> optionalMember = memberRepository.findByEmail(email);
    return optionalMember.orElse(null);
  }

  public void tokenToSessions(String access, String refresh, HttpSession session) {
    session.setAttribute("Authorization", "Bearer " + access);
    session.setAttribute("RefreshToken", refresh);
  }

  // 소셜 로그인 - 토큰 만들어서 헤더에 넣어주기
  public void socialLogin(Member socialUser, HttpSession session){
    String accessToken = tokenProvider.GenerateAccessToken(socialUser);
    String refreshToken = tokenProvider.GenerateRefreshToken(socialUser);
    tokenToSessions(accessToken, refreshToken, session);
  }

  public ResponseEntity<?> createMember(MemberRequestDto requestDto) {
    if (validationUtil.emailExist(requestDto.getEmail())){
      return ResponseEntity.ok("이미 회원가입된 이메일 입니다.");
    }
    if (requestDto.getAuthKey() == null) {
      return ResponseEntity.ok("이메일 인증번호를 적어주세요.");
    }
    if (validationUtil.emailAuth(requestDto)){
      return ResponseEntity.ok("이메일 인증번호가 틀립니다.");
    }
    if (!(validationUtil.passwordCheck(requestDto))){
      return ResponseEntity.ok("비밀번호가 다릅니다.");
    }

    Member member = new Member(requestDto, passwordEncoder.encode(requestDto.getPassword()));
    memberRepository.save(member);
    return ResponseEntity.ok("success");
  }
}
