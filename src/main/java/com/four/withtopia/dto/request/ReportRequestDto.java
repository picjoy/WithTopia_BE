package com.four.withtopia.dto.request;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Data
@RequiredArgsConstructor
public class ReportRequestDto {
    private String toNickname;
    private String byNickname;// 신고 할사람
    private String content; // 신고 내용
    private MultipartFile image;
}
