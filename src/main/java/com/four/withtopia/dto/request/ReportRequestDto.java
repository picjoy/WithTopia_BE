package com.four.withtopia.dto.request;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Data
@RequiredArgsConstructor
public class ReportRequestDto {
    private Long  memberId;
    private String nickname;// 신고 할사람
    private String content; // 신고 내용
    private MultipartFile image;
}
