package com.four.withtopia.api.controller;


import com.four.withtopia.api.service.RoomService;
import com.four.withtopia.config.error.ErrorCode;
import com.four.withtopia.config.expection.PrivateResponseBody;
import com.four.withtopia.config.security.UserDetailsImpl;
import com.four.withtopia.db.domain.Member;
import com.four.withtopia.dto.request.MakeRoomRequestDto;
import io.openvidu.java.client.OpenViduHttpException;
import io.openvidu.java.client.OpenViduJavaClientException;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;


@RestController
@RequiredArgsConstructor
public class RoomController {
    private final RoomService roomService;


    // 방 생성
    @PostMapping("/create/room")
    @ApiOperation(value = "방 생성 메소드")
    public ResponseEntity<PrivateResponseBody> makeRoom(@RequestBody MakeRoomRequestDto makeRoomRequestDto, @AuthenticationPrincipal UserDetailsImpl userDetails)
            throws OpenViduJavaClientException, OpenViduHttpException {
        Member member = ((UserDetailsImpl) userDetails).getMember();
        return new ResponseEntity<>(new PrivateResponseBody(ErrorCode.OK , roomService.createRoom(makeRoomRequestDto, member)) , HttpStatus.OK);

    }

    //전체 방 조회 페이지처리
    @ApiOperation(value = "방 전체 조회 메소드")
    @GetMapping("/rooms/{page}")
    public ResponseEntity<?> getAllRooms(@PathVariable int page){
        return new ResponseEntity<>(new PrivateResponseBody(ErrorCode.OK , roomService.getAllRooms(page)), HttpStatus.OK);
    }

    // 방 접속
    @ApiOperation(value = "일반 멤버 방 접속 메소드")
    @GetMapping("/room/{roomId}")
    public ResponseEntity<PrivateResponseBody> enterRoom(@PathVariable String roomId,@AuthenticationPrincipal UserDetailsImpl userDetails) throws OpenViduJavaClientException, OpenViduHttpException {
        Member member = ((UserDetailsImpl) userDetails).getMember();
        return new ResponseEntity<>(new PrivateResponseBody(ErrorCode.OK,roomService.getRoomData(roomId,member)) , HttpStatus.OK);
    }


    // 방장 나가기
    @ApiOperation(value = "방장 나가기 메소드")
    @DeleteMapping("/room/{roomId}")
    public ResponseEntity<PrivateResponseBody> outRoom(@PathVariable String roomId, @AuthenticationPrincipal UserDetailsImpl userDetails){
        Member member = ((UserDetailsImpl) userDetails).getMember();
        return new ResponseEntity<>(new PrivateResponseBody(ErrorCode.OK, roomService.outRoom(roomId, member)), HttpStatus.OK);
    }

    // 일반 멤버 나가기
    @ApiOperation(value = "일반 멤버 나가기 메소드")
    @PostMapping ("/room/{roomId}/member")
    public ResponseEntity<PrivateResponseBody> outRoomMember(@PathVariable String roomId,@AuthenticationPrincipal UserDetailsImpl userDetails){
        Member member = ((UserDetailsImpl) userDetails).getMember();
        return new ResponseEntity<>(new PrivateResponseBody(ErrorCode.OK, roomService.outRoomMember(roomId,member)), HttpStatus.OK);
    }

    // 방제 수정
    @ApiOperation(value = "방제 수정 메소드")
    @PutMapping("/room/{roomId}")
    public ResponseEntity<PrivateResponseBody> renameRoom(@PathVariable String roomId, @AuthenticationPrincipal UserDetailsImpl userDetails, @RequestBody String roomTitle){
        Member member = ((UserDetailsImpl) userDetails).getMember();
        return new ResponseEntity<>(new PrivateResponseBody(ErrorCode.OK, roomService.renameRoom(roomId, member, roomTitle)), HttpStatus.OK);
    }


    // 키워드로 방 검색
    @ApiOperation(value = "방 찾기 메소드")
    @GetMapping("/rooms/search/{page}")
    public ResponseEntity<PrivateResponseBody> searchRoom(@PathVariable int page, @RequestBody String keyword){
        return new ResponseEntity<>(new PrivateResponseBody(ErrorCode.OK, roomService.searchRoom(keyword,page)), HttpStatus.OK);
    }






}