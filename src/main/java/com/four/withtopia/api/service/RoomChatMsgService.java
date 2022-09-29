package com.four.withtopia.api.service;

import com.four.withtopia.config.error.ErrorCode;
import com.four.withtopia.config.expection.PrivateException;
import com.four.withtopia.db.domain.*;
import com.four.withtopia.db.repository.BenMemberRepository;
import com.four.withtopia.db.repository.MemberRepository;
import com.four.withtopia.db.repository.RoomMemberRepository;
import com.four.withtopia.db.repository.RoomRepository;
import com.four.withtopia.dto.stomp.RoomChatMsgDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
public class RoomChatMsgService {

    private final MemberRepository memberRepository;
    private final BenMemberRepository benMemberRepository;
    private final RoomMemberRepository roomMemberRepository;
    private final RoomRepository roomRepository;

    // 주고받는 메세지
    public RoomChatMsgDto createRoomChatMessage(String roomId, ChatMessage chatMsgDto){

        // 현재 시간 구하기
        DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("MM-dd HH:mm");
        LocalDateTime now = LocalDateTime.now(ZoneId.of("Asia/Seoul"));
        String date = now.format(dateFormat);
        System.out.println("date = " + date);

        // 입장 메세지
        if(chatMsgDto.getType().equals(ChatMessage.MessageType.ENTER)){
            System.out.println("입장");
            RoomChatMsgDto enterMsg =  RoomChatMsgDto.builder()
                    .roomId(roomId)
                    .type(chatMsgDto.getType())
                    .message(chatMsgDto.getSender() + "님이 입장했습니다.")
                    .sender(chatMsgDto.getSender())
                    .date(date)
                    .build();

            System.out.println("enterMsg = " + enterMsg);

            return enterMsg;
        }

        // 나가기 메세지
        if(chatMsgDto.getType().equals(ChatMessage.MessageType.EXIT)){
            System.out.println("나가기");
            RoomChatMsgDto leaveMsg =  RoomChatMsgDto.builder()
                    .roomId(roomId)
                    .type(chatMsgDto.getType())
                    .message(chatMsgDto.getSender() + "님이 나갔습니다.")
                    .sender(chatMsgDto.getSender())
                    .date(date)
                    .build();

            System.out.println("enterMsg = " + leaveMsg);

            return leaveMsg;
        }

        // 강퇴 메세지
        if(chatMsgDto.getType().equals(ChatMessage.MessageType.BEN)){
            System.out.println("강퇴");
            RoomChatMsgDto benMsg =  RoomChatMsgDto.builder()
                    .roomId(roomId)
                    .type(chatMsgDto.getType())
                    .message(chatMsgDto.getReceive() + "님이 강퇴되셨습니다.")
                    .receive(chatMsgDto.getReceive())
                    .date(date)
                    .build();

            System.out.println("enterMsg = " + benMsg);

            // 강퇴 멤버 저장
            Member member = memberRepository.findByNickName(chatMsgDto.getReceive()).orElseThrow(
                    () -> new PrivateException(new ErrorCode(HttpStatus.BAD_REQUEST,"400","사용자가 존재하지 않습니다.")));

            BenMember benMember = BenMember.builder()
                            .memberId(member.getMemberId())
                            .roomId(roomId)
                            .build();
            benMemberRepository.save(benMember);

            //강퇴 멤버 룸멤버 리스트에서 지우기
            // 방이 있는 지 확인
            Room room = roomRepository.findById(roomId).orElseThrow(
                    () -> new PrivateException(new ErrorCode(HttpStatus.BAD_REQUEST,"400","방이 존재하지않습니다."))
            );
            // 룸 멤버 찾기
            RoomMember roomMember = roomMemberRepository.findBySessionIdAndNickname(roomId, chatMsgDto.getReceive()).orElseThrow(
                    () -> new PrivateException(new ErrorCode(HttpStatus.BAD_REQUEST,"400","방에 있는 멤버가 아닙니다."))
            );
            // 룸 멤버 삭제
            roomMemberRepository.delete(roomMember);
            // 룸 멤버 수 변경
            room.updateCntMember(room.getCntMember() -1);
            // 룸 변경사항 저장
            roomRepository.save(room);

            return benMsg;
        }

        RoomChatMsgDto talkMsg =  RoomChatMsgDto.builder()
                .roomId(roomId)
                .type(chatMsgDto.getType())
                .message(chatMsgDto.getMessage())
                .sender(chatMsgDto.getSender())
                .date(date)
                .build();

        System.out.println("talkMsg = " + talkMsg);

        return talkMsg;
    }

}
