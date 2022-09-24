package com.four.withtopia.api.controller;

import com.four.withtopia.api.service.MypageService;
import com.four.withtopia.config.expection.PrivateResponseBody;
import com.four.withtopia.dto.request.ChangePasswordRequestDto;
import com.four.withtopia.dto.request.ProfileUpdateRequestDto;
import com.four.withtopia.util.ResponseUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@RestController
@RequiredArgsConstructor
@RequestMapping(produces = "application/json; charset=utf8")
public class MypageController {
    private final MypageService mypageService;

    @RequestMapping(value = "/member/mypage", method = RequestMethod.GET)
    public ResponseEntity<PrivateResponseBody> getMypage(HttpServletRequest request){
        return new ResponseUtil<>().forSuccess(mypageService.getMypage(request));
    }

    @RequestMapping(value = "/member/mypage", method = RequestMethod.PUT)
    public ResponseEntity<PrivateResponseBody> updateMemberInfo(@RequestBody ProfileUpdateRequestDto requestDto, HttpServletRequest request){
        return new ResponseUtil<>().forSuccess(mypageService.updateMemberInfo(requestDto, request));
    }

    @RequestMapping(value = "/member/leave", method = RequestMethod.PUT)
    public ResponseEntity<PrivateResponseBody> deleteMember(HttpServletRequest request){
        return new ResponseUtil<>().forSuccess(mypageService.deleteMember(request));
    }

    @RequestMapping(value = "/member/mypage/changepw", method = RequestMethod.PUT)
    public ResponseEntity<PrivateResponseBody> ChangePw(@RequestBody ChangePasswordRequestDto requestDto){
        return new ResponseUtil<>().forSuccess(mypageService.changePassword(requestDto));
    }
    @RequestMapping(value = "/image", method = RequestMethod.POST)
    public ResponseEntity<PrivateResponseBody> reissue(@RequestPart MultipartFile multipartFile) throws IOException {
        return new ResponseUtil<>().forSuccess(mypageService.insertImage(multipartFile));
    }

    @RequestMapping(value = "/mypage/image", method = RequestMethod.GET)
    public ResponseEntity<?> getProfileImage(){
        return new ResponseUtil<>().forSuccess(mypageService.getProfileImage());
    }

}