package com.four.withtopia.api.service;

import com.four.withtopia.db.domain.ChatMessage;
import com.four.withtopia.dto.stomp.RoomChatMsgDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
public class RoomChatMsgService {

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
                    .message(chatMsgDto.getSender() + "가 입장했습니다.")
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
                    .message(chatMsgDto.getSender() + "가 나갔습니다.")
                    .sender(chatMsgDto.getSender())
                    .date(date)
                    .build();

            System.out.println("enterMsg = " + leaveMsg);

            return leaveMsg;
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
