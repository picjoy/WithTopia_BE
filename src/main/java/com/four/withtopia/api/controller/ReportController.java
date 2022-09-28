package com.four.withtopia.api.controller;

import com.four.withtopia.api.service.ReportService;
import com.four.withtopia.dto.request.ReportRequestDto;
import com.four.withtopia.util.ResponseUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@RestController
@RequiredArgsConstructor
public class ReportController {
    private final ReportService reportService;

    @PostMapping("/report")
    public ResponseEntity<?> createReport(@ModelAttribute ReportRequestDto requestDto, HttpServletRequest request) throws IOException {
        System.out.println("requestDto.getContent() = " + requestDto.getContent());
        return new ResponseUtil<>().forSuccess(reportService.createReport(requestDto, request));
    }
    @GetMapping("/report")
    public ResponseEntity<?> Report(@RequestParam String sessionID) throws IOException {
        return new ResponseUtil<>().forSuccess(reportService.ShowRoomMember(sessionID));
    }
}
