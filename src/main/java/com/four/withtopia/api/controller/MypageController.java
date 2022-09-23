package com.four.withtopia.api.controller;

import com.four.withtopia.api.service.MypageService;
import com.four.withtopia.dto.request.ChangePasswordRequestDto;
import com.four.withtopia.dto.request.ProfileUpdateRequestDto;
import com.four.withtopia.util.ResponseUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequiredArgsConstructor
@RequestMapping(produces = "application/json; charset=utf8")
public class MypageController {
    private final MypageService mypageService;

    @RequestMapping(value = "/member/mypage", method = RequestMethod.GET)
    public ResponseEntity<?> getMypage(HttpServletRequest request){
        return new ResponseUtil<>().forSuccess(mypageService.getMypage(request));
    }

    @RequestMapping(value = "/member/mypage", method = RequestMethod.PUT)
    public ResponseEntity<?> updateMemberInfo(@RequestBody ProfileUpdateRequestDto requestDto, HttpServletRequest request){
        return new ResponseUtil<>().forSuccess(mypageService.updateMemberInfo(requestDto, request));
    }

    @RequestMapping(value = "/member/leave", method = RequestMethod.PUT)
    public ResponseEntity<?> deleteMember(HttpServletRequest request){
        return new ResponseUtil<>().forSuccess(mypageService.deleteMember(request));
    }

    @RequestMapping(value = "/member/mypage/changepw", method = RequestMethod.PUT)
    public ResponseEntity<?> ChangePw(@RequestBody ChangePasswordRequestDto requestDto){
        return new ResponseUtil<>().forSuccess(mypageService.changePassword(requestDto));
    }

}