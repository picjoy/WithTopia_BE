package com.four.withtopia.api.controller;

import com.four.withtopia.api.service.VoteService;
import com.four.withtopia.dto.request.VoteRequestDto;
import com.four.withtopia.util.ResponseUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequiredArgsConstructor
public class VoteController {

    private final VoteService voteService;

    @PostMapping("/vote")
    public ResponseEntity<?> vote(@RequestBody VoteRequestDto requestDto, HttpServletRequest request){
        return new ResponseUtil<>().forSuccess(voteService.vote(requestDto, request));
    }
}
