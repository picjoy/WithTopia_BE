package com.four.withtopia.api.service;


import com.four.withtopia.config.error.ErrorCode;
import com.four.withtopia.config.expection.PrivateException;
import com.four.withtopia.db.domain.BenMember;
import com.four.withtopia.db.domain.Member;
import com.four.withtopia.db.domain.Room;
import com.four.withtopia.db.domain.RoomMember;
import com.four.withtopia.db.repository.BenMemberRepository;
import com.four.withtopia.db.repository.RoomMemberRepository;
import com.four.withtopia.db.repository.RoomRepository;
import com.four.withtopia.dto.request.MakeRoomRequestDto;
import com.four.withtopia.dto.request.RoomPasswordRequestDto;
import com.four.withtopia.dto.request.RoomTitleRenameDto;
import com.four.withtopia.dto.response.RoomCreateResponseDto;
import com.four.withtopia.dto.response.RoomMemberResponseDto;
import com.four.withtopia.util.MemberCheckUtils;
import io.openvidu.java.client.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;


@Service
@RequiredArgsConstructor
public class RoomService {

    private final RoomRepository roomRepository;
    private final RoomMemberRepository roomMemberRepository;
    private final MemberCheckUtils memberCheckUtils;
    private final BenMemberRepository benMemberRepository;

    // SDK의 진입점인 OpenVidu 개체
    private OpenVidu openVidu;

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
    public RoomCreateResponseDto createRoom(MakeRoomRequestDto makeRoomRequestDto, HttpServletRequest request) throws OpenViduJavaClientException, OpenViduHttpException {

        //토큰 검증 및 멤버 객체 가져오기
        Member member = memberCheckUtils.checkMember(request);

        String Pattern =  "^[a-zA-Z\\d!@#$%^&*]{4,12}$";
        System.out.println(makeRoomRequestDto.getPassword().matches(Pattern));

        if (makeRoomRequestDto.getRoomTitle().length() < 2 || makeRoomRequestDto.getRoomTitle().length() > 14){
            throw new PrivateException(new ErrorCode(HttpStatus.OK, "200","제목 양식에 맞지 않습니다."));
        }


        if(!makeRoomRequestDto.isStatus()&&!makeRoomRequestDto.getPassword().matches(Pattern)){
            throw new PrivateException(new ErrorCode(HttpStatus.OK,"200","비밀번호 양식이 맞지않습니다."));
        }

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
                .password(makeRoomRequestDto.getPassword())
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
                .enterRoomToken(savedRoom.getSessionId())
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
        // 오픈 비두 서버에서 미디어 데이터를 받아올 떄 사용할 토큰을 리턴.
        // 채팅방 생성 후 최초 채팅방 생성자는 채팅방에 즉시 입장할 것으로 예상 -> 채팅방이 보여지기 위한 정보들을 리턴
        return RoomCreateResponseDto.builder()
                .sessionId(savedRoom.getSessionId())
                .roomTitle(savedRoom.getRoomTitle())
                .masterId(savedRoom.getMasterId())
                .maxMember(savedRoom.getMaxMember())
                .cntMember(savedRoom.getCntMember())
                .roomMemberResponseDtoList(roomMemberResponseDtoList)
                .status(savedRoom.isStatus())
                .token(newToken.getToken())
                .password(savedRoom.getPassword())
                .build();
    }

//    // 전체 방 조회하기
//    public Page<Room> getAllRooms(int page) {
//        PageRequest pageable = PageRequest.of(page-1,6);
//
//        Page<Room> allRooms = roomRepository.findAllByOrderByModifiedAtAsc(pageable);
//
//        return allRooms;
//    }

    // 키워드로 채팅방 검색하기
    public Page<Room> searchRoom(String keyword, int page) {
        PageRequest pageable = PageRequest.of(page-1,6);

        System.out.println(keyword);
        Page<Room> searchRoom;
        if (keyword == null || keyword.isBlank() || keyword.isEmpty()) {
            searchRoom = roomRepository.findAllByOrderByModifiedAtAsc(pageable);
            return searchRoom;
        } else {
            if (keyword.length() < 2 || keyword.length() >14){
                throw new PrivateException(new ErrorCode(HttpStatus.OK,"200","검색 양식에 맞지 않습니다."));
            }
            searchRoom = roomRepository.findByRoomTitleContainingOrderByModifiedAtAsc(keyword, pageable);
        }

        // 검색 결과가 없다면
        if (searchRoom.isEmpty()){
            throw new PrivateException(new ErrorCode(HttpStatus.BAD_REQUEST,"400","검색 결과가 없습니다."));
        }

        return searchRoom;

    }

    // 방장 방 나가기
    public String outRoom(String sessionId, HttpServletRequest request){

        //토큰 검증 및 멤버 객체 가져오기
        Member member = memberCheckUtils.checkMember(request);

        // 채팅방 찾기
        Room room = roomRepository.findById(sessionId).orElseThrow(
                () -> new PrivateException(new ErrorCode(HttpStatus.BAD_REQUEST,"400","해당 방이 없습니다.")));

        // 방장인 지 확인
        if (room.validateMember(member)){
            throw new PrivateException(new ErrorCode(HttpStatus.BAD_REQUEST,"400","방장이 아닙니다."));
        }
        roomRepository.delete(room);

        return "Success";
    }

    // 방 접속
    public RoomMemberResponseDto getRoomData(String SessionId, HttpServletRequest request, RoomPasswordRequestDto password) throws OpenViduJavaClientException, OpenViduHttpException {

        //토큰 검증 및 멤버 객체 가져오기
        Member member = memberCheckUtils.checkMember(request);

        // 방이 있는 지 확인
        Room room = roomRepository.findById(SessionId).orElseThrow(
                () -> new PrivateException(new ErrorCode(HttpStatus.BAD_REQUEST,"400","해당 방이 없습니다.")));

        // 방에서 강퇴당한 멤버인지 확인
        BenMember benMember = benMemberRepository.findByMemberIdAndRoomId(member.getMemberId(), SessionId);
        if(benMember != null){
            throw new PrivateException(new ErrorCode(HttpStatus.BAD_REQUEST,"400","강퇴당한 방입니다."));
        }
        // 방 인원 초과 시
        if (room.getCntMember() >= room.getMaxMember()){
            throw new PrivateException(new ErrorCode(HttpStatus.BAD_REQUEST,"400","방이 가득찼습니다."));
        }

        // 룸 멤버 있는 지 확인
        Optional<RoomMember> alreadyRoomMember = roomMemberRepository.findBySessionIdAndNickname(SessionId,member.getNickName());
        if (alreadyRoomMember.isPresent()){
            throw new PrivateException(new ErrorCode(HttpStatus.BAD_REQUEST,"400","이미 입장한 멤버입니다."));
        }

        // 방에 비밀번호 있는 지 확인
        if (!room.isStatus()){  // 방이 private인 지 확인
            if (null == password.getPassword()){    // 패스워드를 입력 안했을 때 에러 발생
                throw new PrivateException(new ErrorCode(HttpStatus.BAD_REQUEST, "400", "비밀번호를 입력해주세요."));
            }
            if (!room.getPassword().equals(password.getPassword())){  // 비밀번호가 틀리면 에러 발생
                throw new PrivateException(new ErrorCode(HttpStatus.BAD_REQUEST,"400", "비밀번호가 틀립니다."));
            }
        }


        //채팅방 입장 시 토큰 발급
        String enterRoomToken = enterRoomCreateSession(member,room.getSessionId());

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
            // 방장일 시
            if (member != null){
                roomMaster = Objects.equals(addRoomMember.getNickname(), member.getNickName());
            }
            // 방장이 아닐 시
            else {
                roomMaster = false;
            }
            roomMemberResponseDtoList.add(new RoomMemberResponseDto(addRoomMember,roomMaster));

        }

        Long currentMember = roomMemberRepository.countAllBySessionId(room.getSessionId());

        room.updateCntMember(currentMember);

        roomRepository.save(room);

        return RoomMemberResponseDto.builder()
                .roomMemberId(roomMember.getRoomMemberId())
                .sessionId(roomMember.getSessionId())
                .member(roomMember.getMember())
                .nickname(roomMember.getNickname())
                .email(roomMember.getEmail())
                .ProfileImage(roomMember.getProfileImage())
                .enterRoomToken(roomMember.getEnterRoomToken())
                .roomMaster(roomMaster)
                .build();
    }

    // 일반 멤버 나가기
    public String outRoomMember(String sessionId, HttpServletRequest request) {

        //토큰 검증 및 멤버 객체 가져오기
        Member member = memberCheckUtils.checkMember(request);

        // 방이 있는 지 확인
        Room room = roomRepository.findById(sessionId).orElseThrow(
                () -> new PrivateException(new ErrorCode(HttpStatus.BAD_REQUEST,"400","방이 존재하지않습니다."))
        );

        // 룸 멤버 찾기
        RoomMember roomMember = roomMemberRepository.findBySessionIdAndNickname(sessionId,member.getNickName()).orElseThrow(
                () -> new PrivateException(new ErrorCode(HttpStatus.BAD_REQUEST,"400","방에 있는 멤버가 아닙니다."))
        );

        // 룸 멤버 삭제
        roomMemberRepository.delete(roomMember);

        // 룸 멤버 수 변경
        room.updateCntMember(room.getCntMember() -1);

        // 룸 변경사항 저장
        roomRepository.save(room);

        return "Success";
    }

    // 방제 수정
    public String renameRoom(String roomId, HttpServletRequest request, RoomTitleRenameDto roomTitle) {

        //토큰 검증 및 멤버 객체 가져오기
        Member member = memberCheckUtils.checkMember(request);

        // 채팅방 찾기
        Room room = roomRepository.findById(roomId).orElseThrow(
                () -> new PrivateException(new ErrorCode(HttpStatus.BAD_REQUEST,"400","방이 존재하지않습니다.")));

        // 방장인 지 확인
        if (room.validateMember(member)){
            throw new PrivateException(new ErrorCode(HttpStatus.BAD_REQUEST,"400","방이 존재하지않습니다."));
        }

        room.rename(roomTitle.getRoomTitle());
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
            throw new PrivateException(new ErrorCode(HttpStatus.BAD_REQUEST,"400","방이 존재하지않습니다."));
        }

        // 2. Openvidu에 유저 토큰 발급 요청 : 오픈비두 서버에 요청 유저가 타겟 채팅방에 입장할 수 있는 토큰을 발급 요청
        //토큰을 가져옴
        return session.createConnection(connectionProperties).getToken();
    }



}
