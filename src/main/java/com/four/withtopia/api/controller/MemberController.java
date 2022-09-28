package com.four.withtopia.api.controller;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.four.withtopia.api.service.MemberService;
import com.four.withtopia.config.expection.PrivateResponseBody;
import com.four.withtopia.config.security.jwt.TokenProvider;
import com.four.withtopia.db.repository.RefreshTokenRepository;
import com.four.withtopia.dto.request.LoginRequestDto;
import com.four.withtopia.dto.request.MemberRequestDto;
import com.four.withtopia.dto.request.NicknameRequestDto;
import com.four.withtopia.util.ResponseUtil;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@RequiredArgsConstructor
@RestController
@RequestMapping(produces = "application/json; charset=utf8")
@CrossOrigin("http://localhost:3000")
public class MemberController {

  private final MemberService memberService;
  private final TokenProvider tokenProvider;
  private final RefreshTokenRepository refreshTokenRepository;

  @ApiOperation(value = "회원가입")
  @RequestMapping(value = "/member/signup", method = RequestMethod.POST)
  public ResponseEntity<PrivateResponseBody> signup(@RequestBody MemberRequestDto requestDto) {
    return new ResponseUtil<>().forSuccess(memberService.createMember(requestDto));
  }

  @ApiOperation(value = "로그인")
  @RequestMapping(value = "/member/login", method = RequestMethod.POST)
  public ResponseEntity<PrivateResponseBody> login(@RequestBody LoginRequestDto requestDto, HttpServletResponse response) {
    return new ResponseUtil<>().forSuccess(memberService.login(requestDto,response));
  }

  @ApiOperation(value = "로그아웃")
  @RequestMapping(value = "/member/logout", method = RequestMethod.POST)
  public ResponseEntity<PrivateResponseBody> logout(HttpServletRequest request) {
    return new ResponseUtil<>().forSuccess(memberService.logout(request));
  }

  @ApiOperation(value = "카카오 로그인")
  @RequestMapping(value = "/member/login/kakao", method = RequestMethod.GET)
  public ResponseEntity<PrivateResponseBody> kakaoLogin(@RequestParam(value="code") String code, HttpServletResponse response) throws JsonProcessingException {
    return new ResponseUtil<>().forSuccess(memberService.kakaoLogin(code, response));
  }

  @ApiOperation(value = "구글 로그인")
  @RequestMapping(value = "/member/login/google", method = RequestMethod.GET)
  public ResponseEntity<PrivateResponseBody> googleLogin(@RequestParam(value="code") String code, HttpServletResponse response) throws JsonProcessingException {
    return new ResponseUtil<>().forSuccess(memberService.googleLogin(code, response));
  }

  @ApiOperation(value = "유저 패스워드 변경")
  @RequestMapping(value = "/member/changepw", method = RequestMethod.GET)
  public ResponseEntity<PrivateResponseBody> changePw(@RequestBody MemberRequestDto requestDto){
    return new ResponseUtil<>().forSuccess(memberService.ChangePw(requestDto));
  }

  @ApiOperation(value = "닉네임 중복 조회")
  @RequestMapping(value = "/member/nickname", method = RequestMethod.POST)
  public ResponseEntity<PrivateResponseBody> existnick(@RequestBody NicknameRequestDto email){
    return new ResponseUtil<>().forSuccess(memberService.existnickname(email.getNickname()));
  }

  @ApiOperation(value = "엑세스 토큰 재발급")
  @RequestMapping(value = "/member/reissue", method = RequestMethod.GET)
  public ResponseEntity<PrivateResponseBody> reissue(HttpServletRequest request, HttpServletResponse response) {
    return new ResponseUtil<>().forSuccess(memberService.reissue(request, response));
  }

  @ApiOperation(value = "계정 정리")
  @RequestMapping(value = "/member/suspend/{memberId}", method = RequestMethod.PUT)
  public void memberSuspend(@PathVariable Long memberId){
    memberService.memberSuspend(memberId);
  }
}
