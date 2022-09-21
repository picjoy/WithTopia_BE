package com.four.withtopia.api.controller;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.four.withtopia.api.service.MemberService;
import com.four.withtopia.config.error.ErrorCode;
import com.four.withtopia.config.expection.PrivateResponseBody;
import com.four.withtopia.config.security.jwt.TokenProvider;
import com.four.withtopia.db.repository.RefreshTokenRepository;
import com.four.withtopia.dto.request.LoginRequestDto;
import com.four.withtopia.dto.request.MemberRequestDto;
import com.four.withtopia.dto.request.NicknameRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@RequiredArgsConstructor
@RestController
@RequestMapping(produces = "application/json; charset=utf8")
public class MemberController {

  private final MemberService memberService;
  private final TokenProvider tokenProvider;
  private final RefreshTokenRepository refreshTokenRepository;

  @RequestMapping(value = "/member/signup", method = RequestMethod.POST)
  public ResponseEntity<?> signup(@RequestBody MemberRequestDto requestDto) {
    return new ResponseEntity<>(new PrivateResponseBody(new ErrorCode(HttpStatus.OK, "0", "정상"), memberService.createMember(requestDto)), HttpStatus.OK);
  }

  @RequestMapping(value = "/member/login", method = RequestMethod.POST)
  public ResponseEntity<?> login(@RequestBody LoginRequestDto requestDto, HttpServletResponse response) {
    return new ResponseEntity<>(new PrivateResponseBody(new ErrorCode(HttpStatus.OK, "0", "정상"), memberService.login(requestDto,response)), HttpStatus.OK);
  }

  @RequestMapping(value = "/member/logout", method = RequestMethod.POST)
  public ResponseEntity<?> logout(HttpServletRequest request) {
    return new ResponseEntity<>(new PrivateResponseBody(new ErrorCode(HttpStatus.OK, "0", "정상"), memberService.logout(request)), HttpStatus.OK);
  }

  @RequestMapping(value = "/member/login/kakao", method = RequestMethod.GET)
  public ResponseEntity<?> kakaoLogin(@RequestParam(value="code") String code, HttpSession session) throws JsonProcessingException {
    return new ResponseEntity<>(new PrivateResponseBody(new ErrorCode(HttpStatus.OK, "0", "정상"),memberService.kakaoLogin(code, session)), HttpStatus.OK);
  }

  @RequestMapping(value = "/member/login/google", method = RequestMethod.GET)
  public ResponseEntity<?> googleLogin(@RequestParam(value="code") String code, HttpSession session) throws JsonProcessingException {
    return new ResponseEntity<>(new PrivateResponseBody(new ErrorCode(HttpStatus.OK, "0", "정상"),memberService.googleLogin(code, session)), HttpStatus.OK);
  }

  @RequestMapping(value = "/member/changepw", method = RequestMethod.GET)
  public ResponseEntity<?> changePw(@RequestBody MemberRequestDto requestDto){
    return new ResponseEntity<>(new PrivateResponseBody(new ErrorCode(HttpStatus.OK, "0", "정상"),memberService.ChangePw(requestDto)), HttpStatus.OK);
  }

  @RequestMapping(value = "/member/nickname", method = RequestMethod.POST)
  public ResponseEntity<?> existnick(@RequestBody NicknameRequestDto email){
    return new ResponseEntity<>(new PrivateResponseBody(new ErrorCode(HttpStatus.OK, "0", "정상"),memberService.existnickname(email.getNickname())), HttpStatus.OK);
  }

  @RequestMapping(value = "/member/reissue", method = RequestMethod.GET)
  public ResponseEntity<?> reissue(HttpServletRequest request, HttpServletResponse response) {
    return new ResponseEntity<>(new PrivateResponseBody(new ErrorCode(HttpStatus.OK, "0", "정상"),memberService.reissue(request, response)), HttpStatus.OK);
  }
}
