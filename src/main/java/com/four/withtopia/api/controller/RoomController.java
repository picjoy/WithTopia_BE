package com.four.withtopia.api.controller;


import com.four.withtopia.api.service.RoomService;
import com.four.withtopia.config.expection.PrivateResponseBody;
import com.four.withtopia.dto.request.MakeRoomRequestDto;
import com.four.withtopia.dto.request.RoomPasswordRequestDto;
import com.four.withtopia.dto.request.RoomTitleRenameDto;
import com.four.withtopia.util.ResponseUtil;
import io.openvidu.java.client.OpenViduHttpException;
import io.openvidu.java.client.OpenViduJavaClientException;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;


@RestController
@RequiredArgsConstructor
@CrossOrigin("http://localhost:3000")
public class RoomController {
    private final RoomService roomService;

    // 방 생성
    @PostMapping("/create/room")
    @ApiOperation(value = "방 생성 메소드")
    public ResponseEntity<PrivateResponseBody> makeRoom(@Valid @RequestBody MakeRoomRequestDto makeRoomRequestDto, HttpServletRequest request)
            throws OpenViduJavaClientException, OpenViduHttpException {
        return new ResponseUtil<>().forSuccess(roomService.createRoom(makeRoomRequestDto, request));
    }

    //전체 방 조회 페이지처리
    @ApiOperation(value = "방 전체 조회 및 검색 메소드")
    @GetMapping("/rooms/{page}")
    public ResponseEntity<PrivateResponseBody> getAllRooms(@PathVariable int page, @RequestParam(required = false) String keyword) {
        return new ResponseUtil<>().forSuccess(roomService.searchRoom(keyword, page));
    }

    // 방 접속
    @ApiOperation(value = "일반 멤버 방 접속 메소드")
    @PostMapping("/room/{roomId}")
    public ResponseEntity<PrivateResponseBody> enterRoom(@PathVariable String roomId, HttpServletRequest request, @RequestBody RoomPasswordRequestDto password) throws OpenViduJavaClientException, OpenViduHttpException {
        return new ResponseUtil<>().forSuccess(roomService.getRoomData(roomId,request,password));
    }


    // 방장 나가기
    @ApiOperation(value = "방장 나가기 메소드")
    @DeleteMapping("/room/{roomId}")
    public ResponseEntity<PrivateResponseBody> outRoom(@PathVariable String roomId, HttpServletRequest request){
        return new ResponseUtil<>().forSuccess( roomService.outRoom(roomId, request));
    }

    // 일반 멤버 나가기
    @ApiOperation(value = "일반 멤버 나가기 메소드")
    @PostMapping ("/room/{roomId}/member")
    public ResponseEntity<PrivateResponseBody> outRoomMember(@PathVariable String roomId, HttpServletRequest request){
        return new ResponseUtil<>().forSuccess(roomService.outRoomMember(roomId,request));
    }

    // 방제 수정
    @ApiOperation(value = "방제 수정 메소드")
    @PutMapping("/room/{roomId}")
    public ResponseEntity<PrivateResponseBody> renameRoom(@PathVariable String roomId, HttpServletRequest request, @RequestBody RoomTitleRenameDto roomTitle){
        return new ResponseUtil<>().forSuccess(roomService.renameRoom(roomId, request, roomTitle));
    }


//    // 키워드로 방 검색
//    @ApiOperation(value = "방 찾기 메소드")
//    @GetMapping("/rooms/search/{page}")
//    public ResponseEntity<?> searchRoom(@PathVariable int page, @RequestParam String keyword){
//        return new ResponseUtil<>().forSuccess(roomService.searchRoom(keyword,page));
//    }

}