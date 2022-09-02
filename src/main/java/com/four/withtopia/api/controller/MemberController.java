package com.four.withtopia.api.controller;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.four.withtopia.api.service.MemberService;
import com.four.withtopia.dto.request.LoginRequestDto;
import com.four.withtopia.dto.request.MemberRequestDto;
import com.four.withtopia.dto.response.ResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

@RequiredArgsConstructor
@RestController
public class MemberController {

  private final MemberService memberService;

  @RequestMapping(value = "/member/signup", method = RequestMethod.POST)
  public ResponseEntity<?> signup(@RequestBody @Valid MemberRequestDto requestDto) {
    return memberService.createMember(requestDto);
  }

  @RequestMapping(value = "/member/login", method = RequestMethod.POST)
  public ResponseDto<?> login(@RequestBody @Valid LoginRequestDto requestDto,
      HttpServletResponse response
  ) {
    return memberService.login(requestDto, response);
  }

//  @RequestMapping(value = "/api/auth/member/reissue", method = RequestMethod.POST)
//  public ResponseDto<?> reissue(HttpServletRequest request, HttpServletResponse response) {
//    return memberService.reissue(request, response);
//  }

  @RequestMapping(value = "/member/logout", method = RequestMethod.POST)
  public ResponseDto<?> logout(HttpServletRequest request) {
    return memberService.logout(request);
  }

  @RequestMapping(value = "/member/login/kakao", method = RequestMethod.GET)
  public ResponseEntity<?> kakaoLogin(@RequestParam(value="code") String code, HttpServletResponse response) throws JsonProcessingException {
    return memberService.kakaoLogin(code, response);
  }

//  @RequestMapping(value = "/member/login/google", method = RequestMethod.GET)
//  public ResponseEntity<?> googleLogin(@RequestParam(name="code") String code, HttpServletResponse response) throws JsonProcessingException {
//    return memberService.googleLogin(code, response);
//  }
}
