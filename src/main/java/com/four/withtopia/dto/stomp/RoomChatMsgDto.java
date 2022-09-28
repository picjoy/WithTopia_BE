package com.four.withtopia.dto.stomp;

import com.four.withtopia.db.domain.ChatMessage;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class RoomChatMsgDto {

    private ChatMessage.MessageType type;
    //채팅방 ID
    private String roomId;
    //보내는 사람
    private String sender;
    // 받는 사람
    private String receive;
    //내용
    private String message;
    private String date;

}
