package com.four.withtopia.db.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

import javax.persistence.Id;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@RedisHash(value = "refresh",timeToLive = 60 * 60 * 24 * 7)
public class RefreshToken {

    @Id
    private String id;
    @Indexed
    private String nickname;
    @Indexed
    private String value;

    public void updateValue(String token) {
        this.value = token;
    }
}
