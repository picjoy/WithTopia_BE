package com.four.withtopia.api.controller;

import com.four.withtopia.api.service.MailSendService;
import com.four.withtopia.config.expection.PrivateResponseBody;
import com.four.withtopia.dto.request.EmailAuthRequestDto;
import com.four.withtopia.dto.request.EmailRequestDto;
import com.four.withtopia.util.ResponseUtil;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.mail.MessagingException;
import java.io.UnsupportedEncodingException;

@RestController
@RequiredArgsConstructor
@RequestMapping(produces = "application/json; charset=utf8")
@CrossOrigin("http://localhost:3000")
public class EmailController {

    private final MailSendService mss;

    // 이메일 인증 신청
    @ApiOperation(value = "이메일 인증 신청")
    @RequestMapping(value = "/member/email/request", method = RequestMethod.POST)
    public ResponseEntity<PrivateResponseBody> emailRequest(@RequestBody EmailRequestDto email) throws MessagingException, UnsupportedEncodingException {
        //DB에 authKey 업데이트
        return new ResponseUtil<>().forSuccess(mss.saveAuth(email.getEmail()));
    }

    //    이메일 인증 번호 비교
    @ApiOperation(value = "이메일 인증")
    @RequestMapping(value = "/member/email/confirm", method = RequestMethod.POST)
    public ResponseEntity<PrivateResponseBody> emailConfirm(@RequestBody EmailAuthRequestDto requestDto) {
        return new ResponseUtil<>().forSuccess(mss.checkAuthKey(requestDto));
    }
}