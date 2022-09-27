package com.four.withtopia.api.service;

import com.four.withtopia.config.error.ErrorCode;
import com.four.withtopia.config.expection.PrivateException;
import com.four.withtopia.db.domain.Member;
import com.four.withtopia.db.domain.Report;
import com.four.withtopia.db.repository.MemberRepository;
import com.four.withtopia.db.repository.ReportRepository;
import com.four.withtopia.dto.request.ReportRequestDto;
import com.four.withtopia.util.InsertImageUtil;
import com.four.withtopia.util.MailUtils;
import com.four.withtopia.util.MemberCheckUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.time.LocalDate;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

@Service
@RequiredArgsConstructor
public class ReportService {
    private final ReportRepository reportRepository;
    private final MemberCheckUtils memberCheckUtils;
    private final InsertImageUtil insertImageUtil;
    private final JavaMailSenderImpl mailSender;

    @Transactional
    public String createReport(ReportRequestDto requestDto, HttpServletRequest request) throws IOException {

        // 토큰 검증
        Member reportBy = memberCheckUtils.checkMember(request);

        if(requestDto.getImage() != null){
            String imgUrl = insertImageUtil.insertImage(requestDto.getImage());
            Report newReport = Report.builder()
                    .reportTo(requestDto.getNickname())
                    .reportToId(requestDto.getMemberId())
                    .reportBy(reportBy.getNickName())
                    .reportById(reportBy.getMemberId())
                    .content(requestDto.getContent())
                    .reportImg(imgUrl)
                    .build();

            reportRepository.save(newReport);
            sendReportMail(newReport);

            return "success";
        }

        // report 추가하기
        Report newReport = Report.builder()
                .reportTo(requestDto.getNickname())
                .reportToId(requestDto.getMemberId())
                .reportBy(reportBy.getNickName())
                .reportById(reportBy.getMemberId())
                .content(requestDto.getContent())
                .reportImg(null)
                .build();

        reportRepository.save(newReport);
        sendReportMail(newReport);

        return "success";
    }

    public void sendReportMail(Report newReport){
        // 신고 내역 관리자에게 메일 보내기
        try {
            MailUtils sendMail = new MailUtils(mailSender);
            sendMail.setSubject("WithTopia - 신고 내역 안내 메일");
            sendMail.setText(new StringBuffer().append("<h1>[신고 내역 안내]</h1>")
                    .append("<br>")
                    .append("<p>아래의 신고 내역을 알려드립니다.</p>")
                    .append("<span>신고자    : </span>").append(newReport.getReportBy())
                    .append("<br>")
                    .append("<span>당사자    : </span>").append(newReport.getReportTo())
                    .append("<br>")
                    .append("<span>신고 내용  : </span>").append(newReport.getContent())
                    .append("<br>")
                    .append("<span>첨부이미지 : </span>").append(newReport.getReportImg())
                    .toString());
            sendMail.setFrom("WithTopia", "Admin");
            sendMail.setTo("wwithtopia404@gmail.com");
            sendMail.send();
        } catch (MessagingException | UnsupportedEncodingException e) {
            throw new PrivateException(new ErrorCode(HttpStatus.BAD_REQUEST,"400","메일 발송에 실패했습니다."));
        }
    }

}
