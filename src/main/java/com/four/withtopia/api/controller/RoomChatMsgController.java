package com.four.withtopia.api.controller;

import com.four.withtopia.api.service.RoomChatMsgService;
import com.four.withtopia.db.domain.ChatMessage;
import com.four.withtopia.dto.stomp.RoomChatMsgDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

//@Slf4j
//@RestController
//@RequiredArgsConstructor
//public class RoomChatMsgController {
//
//    private final SimpMessageSendingOperations sendingOperations;
//    private final RoomChatMsgService roomChatMsgService;
//
//    @MessageMapping("/chat/{roomId}")
//    public void message(@DestinationVariable Long roomId, RoomChatMsgDto chatDto ){
//        chatDto = roomChatMsgService.createMessage(roomId, chatDto);
//        System.out.println("보내기 = " + roomId +  "번 방에서 " + chatDto.getSender() + "가" + chatDto.getMessage() + "라고 말함");
//        sendingOperations.convertAndSend("/topic/room/" + roomId, chatDto);
//    }
//
//}

@RestController
@RequiredArgsConstructor
public class RoomChatMsgController {

    private final SimpMessageSendingOperations sendingOperations;
    private final RoomChatMsgService roomChatMsgService;

    @MessageMapping("/chat/{roomId}")
    public void chat(@DestinationVariable Long roomId, @RequestBody ChatMessage message){
        RoomChatMsgDto chatMessage = roomChatMsgService.createRoomChatMessage(roomId, message);
        System.out.println("chatMessage = " + chatMessage.getSender() + "가 " + chatMessage.getMessage() + "라고 말함");
        sendingOperations.convertAndSend("/topic/chat/"+ roomId, chatMessage);
    }
}
