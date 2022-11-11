package com.four.withtopia;

import com.four.withtopia.api.service.RoomService;
import com.four.withtopia.config.error.ErrorCode;
import com.four.withtopia.config.expection.PrivateException;
import com.four.withtopia.db.domain.BenMember;
import com.four.withtopia.db.domain.Member;
import com.four.withtopia.db.domain.Room;
import com.four.withtopia.db.domain.RoomMember;
import com.four.withtopia.db.repository.MemberRepository;
import com.four.withtopia.db.repository.RoomMemberRepository;
import com.four.withtopia.db.repository.RoomRepository;

import com.four.withtopia.dto.response.RoomMemberResponseDto;

import io.openvidu.java.client.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@SpringBootTest
class WithTopiaApplicationTests {

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    RoomRepository roomRepository;

    @Autowired
    RoomMemberRepository roomMemberRepository;

    @Autowired
    RoomService roomService;

    @Test
    @Order(1)
    @DisplayName("멤버 방 접속 테스트 코드 ")
    void getRoomData() throws OpenViduJavaClientException, OpenViduHttpException {
        getRoomDataTest("ses_THrrpVd0mv","멈뭄미");
        getRoomDataTest("ses_THrrpVd0mv","오리");

    }


    // 방 접속
    void getRoomDataTest(String SessionId, String memberNickname) throws OpenViduJavaClientException, OpenViduHttpException {

        //토큰 검증 및 멤버 객체 가져오기
        Member member = memberRepository.findByNickName(memberNickname).orElseThrow(
                ()-> new PrivateException(new ErrorCode(HttpStatus.BAD_REQUEST,"400","사용자를 찾을 수 없습니다."))
        );

        // 방이 있는 지 확인
        Room room = roomRepository.findById(SessionId).orElseThrow(
                () -> new PrivateException(new ErrorCode(HttpStatus.BAD_REQUEST,"400","해당 방이 없습니다.")));

        // 방 인원 초과 시
        if (room.getCntMember() >= room.getMaxMember()){
            throw new PrivateException(new ErrorCode(HttpStatus.BAD_REQUEST,"400","방이 가득찼습니다."));
        }

        // 룸 멤버 있는 지 확인
        Optional<RoomMember> alreadyRoomMember = roomMemberRepository.findBySessionIdAndNickname(SessionId,member.getNickName());
        if (alreadyRoomMember.isPresent()){
            throw new PrivateException(new ErrorCode(HttpStatus.BAD_REQUEST,"400","이미 입장한 멤버입니다."));
        }


        //채팅방 입장 시 토큰 발급
        String enterRoomToken = roomService.enterRoomCreateSession(member,room.getSessionId());

        // 채팅방 인원
        RoomMember roomMember = RoomMember.builder()
                .sessionId(room.getSessionId())
                .member(member.getMemberId())
                .nickname(member.getNickName())
                .email(member.getEmail())
                .ProfileImage(member.getProfileImage())
                .enterRoomToken(enterRoomToken)
                .build();

        // 채팅방 인원 저장하기
        roomMemberRepository.save(roomMember);

        boolean roomMaster = false;
        List<RoomMember> roomMemberList = roomMemberRepository.findAllBySessionId(room.getSessionId());

        List<RoomMemberResponseDto> roomMemberResponseDtoList = new ArrayList<>();

        // 채팅방 인원 추가
        for (RoomMember addRoomMember : roomMemberList){
            roomMemberResponseDtoList.add(new RoomMemberResponseDto(addRoomMember,roomMaster));

        }

        Long currentMember = roomMemberRepository.countAllBySessionId(room.getSessionId());

        room.updateCntMember(currentMember);

        roomRepository.save(room);


    }

    @Test
    void contextLoads() {
    }

}
