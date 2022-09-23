package com.four.withtopia.api.service;

import com.four.withtopia.config.error.ErrorCode;
import com.four.withtopia.config.expection.PrivateException;
import com.four.withtopia.db.domain.EmailAuth;
import com.four.withtopia.db.repository.EmailAuthRepository;
import com.four.withtopia.dto.request.EmailAuthRequestDto;
import com.four.withtopia.util.MailUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import java.io.UnsupportedEncodingException;
import java.util.Objects;
import java.util.Random;

@Service("mss")
@RequiredArgsConstructor
public class MailSendService {

    private final RedisTemplate<String, Object> redisTemplate;
    private final JavaMailSenderImpl mailSender;
    private int size;
    private final EmailAuthRepository emailAuthRepository;



    //인증키 생성
    private String getKey(int size) {
        this.size = size;
        return getAuthCode();
    }

    //인증코드 난수 발생
    private String getAuthCode() {
        Random random = new Random();
        StringBuffer buffer = new StringBuffer();
        int num = 0;

        while(buffer.length() < size) {
            num = random.nextInt(10);
            buffer.append(num);
        }

        return buffer.toString();
    }

    //인증메일 보내기
//    public String sendAuthMail(String email) throws MessagingException, UnsupportedEncodingException {
//        //6자리 난수 인증번호 생성
//        String authKey = getKey(6);
//
//        //인증메일 보내기
//        try {
//            MailUtils sendMail = new MailUtils(mailSender);
//            sendMail.setSubject("회원가입 이메일 인증");
//            sendMail.setText(new StringBuffer().append("<h1>[이메일 인증]</h1>")
//                    .append("<p>아래 번호를 인증 창에 붙여넣어주세요.</p>")
//                    .append("<br>")
//                    .append("<h1>").append(authKey).append("</h1>")
//                    .toString());
//            sendMail.setFrom("WithTopia", "Admin");
//            sendMail.setTo(email);
//            sendMail.send();
//        } catch (MessagingException | UnsupportedEncodingException e) {
//
//
//        }
//        return authKey;
//    }

    public String saveAuth(String email) throws MessagingException, UnsupportedEncodingException {
        String emailpatern = "^[a-zA-Z0-9]+@[a-zA-Z0-9-]+[a-zA-Z0-9-.]+$";
        if (!email.matches(emailpatern)){
            throw new PrivateException(new ErrorCode(HttpStatus.BAD_REQUEST,"400","이메일 양식을 맞춰주세요"));
        }
        //임의의 authKey 생성 & 이메일 발송
        //6자리 난수 인증번호 생성
        String authKey = getKey(6);
        //인증메일 보내기
        try {
            MailUtils sendMail = new MailUtils(mailSender);
            sendMail.setSubject("회원가입 이메일 인증");
            sendMail.setText(new StringBuffer().append("<h1 style='text-align: center;'>[이메일 인증]</h1>")
                    .append("<p style='text-align: center;'>아래 번호를 인증 창에 붙여넣어주세요.</p>")
                    .append("<br>")
                    .append("<h1 style='text-align: center;'>").append(authKey).append("</h1>")
                    .toString());
            sendMail.setFrom("WithTopia", "Admin");
            sendMail.setTo(email);
            sendMail.send();
        } catch (MessagingException | UnsupportedEncodingException e) {
            throw new PrivateException(new ErrorCode(HttpStatus.BAD_REQUEST,"400","메일 발송에 실패했습니다."));
        }
        EmailAuth emailAuth = new EmailAuth(email,authKey);
        // authKey : email 정보 저장
        EmailAuth origin = emailAuthRepository.findByEmail(emailAuth.getEmail());
        if (!(origin == null)) {
            origin.Update(emailAuth.getAuth());
            emailAuthRepository.save(origin);
        } else {
            emailAuthRepository.save(emailAuth);
        }
        return "이메일 전송 완료";
    }

    public boolean checkAuthKey(EmailAuthRequestDto requestDto) {
        boolean confirm = false;
        EmailAuth origin = emailAuthRepository.findByEmail(requestDto.getEmail());
        if (!(origin == null)) {
            if (Objects.equals(origin.getAuth(), requestDto.getAuthKey())){
                confirm = true;
            }
        }
        return confirm;
    }
}
