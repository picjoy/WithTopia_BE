package com.four.withtopia.api.controller;

import com.four.withtopia.api.service.RoomChatMsgService;
import com.four.withtopia.api.service.chat.RedisPublisher;
import com.four.withtopia.api.service.chat.RedisSubscriber;
import com.four.withtopia.db.domain.ChatMessage;
import com.four.withtopia.dto.stomp.RoomChatMsgDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@CrossOrigin("http://localhost:3000")
public class RoomChatMsgController {
    private final RoomChatMsgService roomChatMsgService;
    private final RedisMessageListenerContainer redisMessageListenerContainer;
    private final RedisSubscriber redisSubscriber;
    private final RedisPublisher redisPublisher;

    @MessageMapping("/chat/{roomId}")
    public void chat(@DestinationVariable String roomId, @RequestBody ChatMessage message){
        System.out.println("send는 성공");
        RoomChatMsgDto chatMessage = roomChatMsgService.createRoomChatMessage(roomId, message);
        ChannelTopic topic = new ChannelTopic(roomId);
        redisMessageListenerContainer.addMessageListener(redisSubscriber, topic);
        redisPublisher.publish(topic, chatMessage);
    }
}
