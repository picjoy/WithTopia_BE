package com.four.withtopia.config.stomp;

import com.four.withtopia.config.security.jwt.TokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.stereotype.Component;

import static org.springframework.messaging.simp.stomp.StompCommand.CONNECT;
import static org.springframework.messaging.simp.stomp.StompCommand.DISCONNECT;

@Slf4j
@Component
@RequiredArgsConstructor
public class StompHandler implements ChannelInterceptor {
    private final TokenProvider tokenProvider;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);

        StompCommand command = accessor.getCommand();
        if (command == CONNECT) {
            checkToken(accessor);

            log.info("connection success");
        }
        else if (command == DISCONNECT) { // Websocket 연결 종료
            log.info("DISCONNECT");
        }

        return message;
    }

    private void checkToken(StompHeaderAccessor accessor) {
        Object accessToken = accessor.getNativeHeader("Authorization");
        System.out.println("websocket accessToken = " + accessToken);

        // 엑세스토큰이 없거나 토큰 검증에 실패한 경우
        if (accessToken == null) {
            log.error("connection fail");
            throw new IllegalArgumentException("토큰이 없습니다.");
        }

        String access = accessToken.toString();
        if(!tokenProvider.validateToken(access.substring(7))){
            log.error("connection fail");
            throw new IllegalArgumentException("토큰을 확인해 주세요.");
        }
    }
}
