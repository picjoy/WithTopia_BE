package com.four.withtopia.api.service;


import com.four.withtopia.config.error.ErrorCode;
import com.four.withtopia.config.expection.PrivateException;
import com.four.withtopia.db.domain.Member;
import com.four.withtopia.db.domain.Room;
import com.four.withtopia.db.domain.RoomMember;
import com.four.withtopia.db.repository.RoomMemberRepository;
import com.four.withtopia.db.repository.RoomRepository;
import com.four.withtopia.dto.request.MakeRoomRequestDto;
import com.four.withtopia.dto.response.RoomCreateResponseDto;
import com.four.withtopia.dto.response.RoomMemberResponseDto;
import io.openvidu.java.client.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;


@Service
@RequiredArgsConstructor
public class RoomService {

    private final RoomRepository roomRepository;
    private final RoomMemberRepository roomMemberRepository;

    // SDK의 진입점인 OpenVidu 개체
    private OpenVidu openVidu;
    // 세션 이름과 OpenVidu 세션 개체를 페어링하기 위한 컬렉션
    private Map<String, Session> mapSessions = new ConcurrentHashMap<>();

    // OpenVidu 서버가 수신하는 URL
    @Value("${openvidu.url}")
    private String OPENVIDU_URL;

    // OpenVidu 서버와 공유되는 비밀
    @Value("${openvidu.secret}")
    private String OPENVIDU_SECRET;

    @PostConstruct
    public OpenVidu openVidu() {
        return openVidu = new OpenVidu(OPENVIDU_URL, OPENVIDU_SECRET);
    }

    // 방 생성
    // 완성 -> 배포해서 프론트가 테스트
    public RoomCreateResponseDto createRoom(MakeRoomRequestDto makeRoomRequestDto, Member member) throws OpenViduJavaClientException, OpenViduHttpException {

        // 새로운 채팅방 생성
        RoomCreateResponseDto newToken = createNewToken(member);
        // 생성된 토큰 확인
        System.out.println("New Token :" + newToken);

        // 채팅방 빌드
        Room room = Room.builder()
                .sessionId(newToken.getSessionId())
                .roomTitle(makeRoomRequestDto.getRoomTitle())
                .masterId(member.getNickName())
                .maxMember(makeRoomRequestDto.getMaxMember())
                .status(makeRoomRequestDto.isStatus())
                .build();


        // 채팅방 저장
        Room savedRoom = roomRepository.save(room);

        // 채팅방 인원
        RoomMember roomMembers = RoomMember.builder()
                .sessionId(savedRoom.getSessionId())
                .member(member.getMemberId())
                .nickname(member.getNickName())
                .email(member.getEmail())
                .ProfileImage(member.getProfileImage())
                .build();

        // 채팅방 인원 저장하기
        roomMemberRepository.save(roomMembers);

        boolean roomMaster;
        List<RoomMember> roomMemberList = roomMemberRepository.findAllBySessionId(savedRoom.getSessionId());

        List<RoomMemberResponseDto> roomMemberResponseDtoList = new ArrayList<>();
        // 채팅방 인원 추가
        for (RoomMember roomMember : roomMemberList){
            // 방장일 시
            if (member != null){
                roomMaster = Objects.equals(roomMember.getNickname(), member.getNickName());
            }
            // 방장이 아닐 시
            else {
                roomMaster = false;
            }
            roomMemberResponseDtoList.add(new RoomMemberResponseDto(roomMember,roomMaster));

        }


        Long currentMember = roomMemberRepository.countAllBySessionId(savedRoom.getSessionId());

        room.updateCntMember(currentMember);


        roomRepository.save(room);

        // 저장된 채팅방의 roomId는 OpenVidu 채팅방의 세션 아이디로써 생성 후 바로 해당 채팅방의 세션 아이디와
        // 오픈 비두 서버에서 미디어 데이터를 받아올 떄 사용할 토큰을 리턴해줍니다.
        // 채팅방 생성 후 최초 채팅방 생성자는 채팅방에 즉시 입장할 것으로 예상 -> 채팅방이 보여지기 위한 정보들을 리턴해줘야할 것 같습니다.
        // TODO: 정확한 와이어 프레임을 확인하지 못했으나 프론트에서 화면에 보여지기위한 데이터가 부족하므로 프론트와 함께 API 명세 재작성 요함 -> ex) 체팅방 명
        return RoomCreateResponseDto.builder()
                .sessionId(savedRoom.getSessionId())
                .roomTitle(savedRoom.getRoomTitle())
                .masterId(savedRoom.getMasterId())
                .maxMember(savedRoom.getMaxMember())
                .cntMember(savedRoom.getCntMember())
                .roomMemberResponseDtoList(roomMemberResponseDtoList)
                .status(savedRoom.isStatus())
                .token(newToken.getToken())
                .build();
    }

    // 전체 방 조회하기
    public Page<Room> getAllRooms(int page) {
        PageRequest pageable = PageRequest.of(page-1,7);

        Page<Room> all = roomRepository.findAll(pageable);



        return all;
    }

    // 방장 방 나가기
    public void outRoom(String sessionId, Member member){

        // 채팅방 찾기
        Room room = roomRepository.findById(sessionId).orElseThrow(
                () -> new PrivateException(ErrorCode.NOT_FOUND_ROOM));

        // 방장인 지 확인
        if (room.validateMember(member)){
            throw new PrivateException(ErrorCode.MEMBER_NOT_AUTH_ERROR_ROOM);
        }
        roomRepository.delete(room);
    }

    // 방 접속
    public RoomMemberResponseDto getRoomData(String SessionId, Member member) throws OpenViduJavaClientException, OpenViduHttpException {

        // 방이 있는 지 확인
        Room room = roomRepository.findById(SessionId).orElseThrow(
                () -> new PrivateException(ErrorCode.NOT_FOUND_ROOM));

        // 방 인원 초과 시
        if (room.getCntMember() > room.getMaxMember()){
            throw new PrivateException(ErrorCode.ROOM_IS_FULL);
        }

        //채팅방 입장 시 토큰 발급
        enterRoomCreateSession(member,room.getSessionId());

        // 채팅방 인원
        RoomMember roomMembers = RoomMember.builder()
                .sessionId(room.getSessionId())
                .member(member.getMemberId())
                .nickname(member.getNickName())
                .email(member.getEmail())
                .ProfileImage(member.getProfileImage())
                .build();

        // 채팅방 인원 저장하기
        roomMemberRepository.save(roomMembers);

        boolean roomMaster = false;
        List<RoomMember> roomMemberList = roomMemberRepository.findAllBySessionId(room.getSessionId());

        List<RoomMemberResponseDto> roomMemberResponseDtoList = new ArrayList<>();

        // 채팅방 인원 추가
        for (RoomMember roomMember : roomMemberList){
            // 방장일 시
            if (member != null){
                roomMaster = Objects.equals(roomMember.getNickname(), member.getNickName());
            }
            // 방장이 아닐 시
            else {
                roomMaster = false;
            }
            roomMemberResponseDtoList.add(new RoomMemberResponseDto(roomMember,roomMaster));

        }

        Long currentMember = roomMemberRepository.countAllBySessionId(room.getSessionId());

        room.updateCntMember(currentMember);

        roomRepository.save(room);

        return RoomMemberResponseDto.builder()
                .roomMemberId(roomMembers.getRoomMemberId())
                .sessionId(roomMembers.getSessionId())
                .member(roomMembers.getMember())
                .nickname(roomMembers.getNickname())
                .email(roomMembers.getEmail())
                .ProfileImage(roomMembers.getProfileImage())
                .roomMaster(roomMaster)
                .build();
    }
/*
    // 일반 멤버 나가기
    public void outRoomMember(String sessionId, Member member) {
        // 방이 있는 지 확인
        Room room = roomRepository.findById(sessionId).orElseThrow(
                () -> new PrivateException(ErrorCode.NOT_FOUND_ROOM));

        roomMemberRepository.delete(member);

    }
*/


    // 방제 수정
    public String renameRoom(String roomId, Member member, String roomTitle) {
        // 채팅방 찾기
        Room room = roomRepository.findById(roomId).orElseThrow(
                () -> new PrivateException(ErrorCode.NOT_FOUND_ROOM));

        // 방장인 지 확인
        if (room.validateMember(member)){
            throw new PrivateException(ErrorCode.MEMBER_NOT_AUTH_ERROR_ROOM);
        }

        room.rename(roomTitle);
        roomRepository.save(room);

        // 방제 바꾸기
        return room.getRoomTitle();

    }
    // 채팅방 생성 시 토큰 발급
    private RoomCreateResponseDto createNewToken(Member member) throws OpenViduJavaClientException, OpenViduHttpException {

        // 사용자 연결 시 닉네임 전달
        String serverData = member.getNickName();

        // serverData을 사용하여 connectionProperties 객체를 빌드
        ConnectionProperties connectionProperties = new ConnectionProperties.Builder().type(ConnectionType.WEBRTC).data(serverData).build();

        // 새로운 OpenVidu 세션(채팅방) 생성
        Session session = openVidu.createSession();

        String token = session.createConnection(connectionProperties).getToken();

        return RoomCreateResponseDto.builder()
                .sessionId(session.getSessionId()) //리턴해주는 해당 세션아이디로 다른 유저 채팅방 입장시 요청해주시면 됩니다.
                .token(token) //이 토큰으로 오픈비두에 해당 유저의 화상 미디어 정보를 받아주세요
                .build();
    }

    //채팅방 입장 시 토큰 발급
    private String enterRoomCreateSession(Member member, String sessionId) throws OpenViduJavaClientException, OpenViduHttpException {
        String serverData = member.getNickName();

        //serverData을 사용하여 connectionProperties 객체를 빌드
        ConnectionProperties connectionProperties = new ConnectionProperties.Builder().type(ConnectionType.WEBRTC).data(serverData).build();

        openVidu.fetch();

        //오픈비두에 활성화된 세션을 모두 가져와 리스트에 담음
        List<Session> activeSessionList = openVidu.getActiveSessions();

        // 1. Request : 다른 유저가 타겟 채팅방에 입장하기 위한 타겟 채팅방의 세션 정보 , 입장 요청하는 유저 정보

        Session session = null;

        //활성화된 session의 sessionId들을 registerReqChatRoom에서 리턴한 sessionId(입장할 채팅방의 sessionId)와 비교
        //같을 경우 해당 session으로 새로운 토큰을 생성
        for (Session getSession : activeSessionList) {
            if (getSession.getSessionId().equals(sessionId)) {
                session = getSession;
                break;
            }
        }
        if (session == null){
            throw new PrivateException(ErrorCode.NOT_FOUND_ROOM);
        }

        // 2. Openvidu에 유저 토큰 발급 요청 : 오픈비두 서버에 요청 유저가 타겟 채팅방에 입장할 수 있는 토큰을 발급해주세요 요청한다.
        //토큰을 가져옴
        return session.createConnection(connectionProperties).getToken();
    }



}
