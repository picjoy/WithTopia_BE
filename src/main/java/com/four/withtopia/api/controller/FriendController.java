package com.four.withtopia.api.controller;

import com.four.withtopia.api.service.FriendService;
import com.four.withtopia.config.error.ErrorCode;
import com.four.withtopia.config.expection.PrivateResponseBody;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequiredArgsConstructor
public class FriendController {

    private final FriendService friendService;

    @ApiOperation(value = "친구 추가 메소드")
    @PostMapping("/friend")
    public ResponseEntity<PrivateResponseBody> makeFriend(@RequestBody String friendName, HttpServletRequest request){
        return new ResponseEntity<>(new PrivateResponseBody(ErrorCode.OK, friendService.makeFriend(friendName, request)), HttpStatus.OK);
    }

    @ApiOperation(value = "친구 삭제 메서드")
    @DeleteMapping("/friend")
    public ResponseEntity<PrivateResponseBody> deleteFriend(@RequestBody String friendName, HttpServletRequest request){
        return new ResponseEntity<>(new PrivateResponseBody(ErrorCode.OK, friendService.deleteFriend(friendName, request)), HttpStatus.OK);
    }

    @ApiOperation(value = " 전체 친구 조회 메서드")
    @GetMapping("/friends/{page}")
    public ResponseEntity<PrivateResponseBody> getAllFriends(@PathVariable int page, HttpServletRequest request){
        return new ResponseEntity<>(new PrivateResponseBody(ErrorCode.OK, friendService.getAllFriends(page, request)), HttpStatus.OK);
    }


}
