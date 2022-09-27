package com.four.withtopia.api.service.chat;

import com.four.withtopia.db.domain.ChatMessage;
import com.four.withtopia.dto.stomp.RoomChatMsgDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class RedisPublisher {
    private final RedisTemplate<String, Object> redisTemplate;

    public void publish(ChannelTopic topic, RoomChatMsgDto message) {
        System.out.println("redis publish message = " + message);
        System.out.println("redis publish topic =" + topic.getTopic());
        redisTemplate.convertAndSend(topic.getTopic(), message);
    }
}
