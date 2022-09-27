package com.four.withtopia.api.service;

import com.four.withtopia.db.domain.BenMember;
import com.four.withtopia.db.domain.ChatMessage;
import com.four.withtopia.db.domain.Member;
import com.four.withtopia.db.repository.BenMemberRepository;
import com.four.withtopia.db.repository.MemberRepository;
import com.four.withtopia.dto.stomp.RoomChatMsgDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RoomChatMsgService {

    private final MemberRepository memberRepository;
    private final BenMemberRepository benMemberRepository;

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
                    .date(date)
                    .build();

            System.out.println("enterMsg = " + benMsg);

            // 강퇴 멤버 저장
            Optional<Member> member = memberRepository.findByNickName(chatMsgDto.getReceive());
            BenMember benMember = BenMember.builder()
                            .memberId(member.get().getMemberId())
                            .roomId(chatMsgDto.getRoomId())
                            .build();
            benMemberRepository.save(benMember);

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
