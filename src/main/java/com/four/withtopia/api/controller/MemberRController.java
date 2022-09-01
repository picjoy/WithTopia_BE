package com.four.withtopia.api.controller;

import com.four.withtopia.api.service.MailSendService;
import com.four.withtopia.api.service.MemberService;
import com.four.withtopia.db.domain.EmailAuth;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MemberRController {
    @Autowired
    private MemberService memberService;
    @Autowired
    private MailSendService mss;


    @RequestMapping("/member/email/request")
    public void signUp(@RequestBody String email){

        //임의의 authKey 생성 & 이메일 발송
        String authKey = mss.sendAuthMail(email);
        EmailAuth emailAuth = new EmailAuth(email,authKey);
        //DB에 authKey 업데이트
        mss.saveAuth(emailAuth);
    }

//    @GetMapping("/member/email/confirm")
//    public ModelAndView signUpConfirm(@RequestParam Map<String, String> map, ModelAndView mav){
//
//    }
}