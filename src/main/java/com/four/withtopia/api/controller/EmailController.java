package com.four.withtopia.api.controller;

import com.four.withtopia.api.service.MailSendService;
import com.four.withtopia.api.service.MemberService;
import com.four.withtopia.db.domain.EmailAuth;
import com.four.withtopia.dto.request.EmailAuthRequestDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class EmailController {
    @Autowired
    private MemberService memberService;
    @Autowired
    private MailSendService mss;

// 이메일 인증 신청
    @RequestMapping("/member/email/request")
    public ResponseEntity<?> emailRequest(@RequestBody String email){

        //임의의 authKey 생성 & 이메일 발송
        String authKey = mss.sendAuthMail(email);
        EmailAuth emailAuth = new EmailAuth(email,authKey);

        //DB에 authKey 업데이트
        return mss.saveAuth(emailAuth);
    }

//    이메일 인증 번호 비교
    @GetMapping("/member/email/confirm")
    public ResponseEntity<?> emailConfirm(@RequestBody EmailAuthRequestDto requestDto){
        return mss.checkAuthKey(requestDto);
    }
}