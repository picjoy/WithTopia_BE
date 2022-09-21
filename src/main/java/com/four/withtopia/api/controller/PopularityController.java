package com.four.withtopia.api.controller;

import com.four.withtopia.api.service.PopularityService;
import com.four.withtopia.util.ResponseUtil;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class PopularityController {

    private final PopularityService popularityService;

    // 인기 상위 멤버
    @ApiOperation(value = "인기 상위 멤버 3명 조회")
    @GetMapping("/top")
    public ResponseEntity<?> topMember(){
        return new ResponseUtil<>().forSuccess(popularityService.topMember());
    }

    // 전체 멤버 랭킹
    @ApiOperation(value = "전체 멤버 랭킹 조회")
    @GetMapping("/rank/{page}")
    public ResponseEntity<?> totalMemberRank(@PathVariable int page){
        return new ResponseUtil<>().forSuccess(popularityService.totalMemberRank(page));
    }
}
