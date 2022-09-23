package com.four.withtopia.api.controller;

import com.four.withtopia.api.service.FriendService;
import com.four.withtopia.util.ResponseUtil;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequiredArgsConstructor
public class FriendController {

    private final FriendService friendService;

    @ApiOperation(value = "친구 추가 메소드")
    @PostMapping("/friend")
    public ResponseEntity<?> makeFriend(@RequestBody String friendName, HttpServletRequest request){
        return new ResponseUtil<>().forSuccess(friendService.makeFriend(friendName, request));
    }

    @ApiOperation(value = "친구 삭제 메서드")
    @DeleteMapping("/friend")
    public ResponseEntity<?> deleteFriend(@RequestBody String friendName, HttpServletRequest request){
        return new ResponseUtil<>().forSuccess(friendService.deleteFriend(friendName, request));
    }

    @ApiOperation(value = " 전체 친구 조회 메서드")
    @GetMapping("/friends/{page}")
    public ResponseEntity<?> getAllFriends(@PathVariable int page, HttpServletRequest request){
        return new ResponseUtil<>().forSuccess(friendService.getAllFriends(page, request));
    }


}
