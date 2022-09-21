package com.four.withtopia.db.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;
import org.springframework.data.redis.core.index.Indexed;


@NoArgsConstructor
@Getter
@Setter
@RedisHash(value = "auth")
public class EmailAuth {

    @Id
    @Indexed
    private String email;
    @Indexed
    private String Auth;

    @TimeToLive
    private long expiration;

    public EmailAuth(String email, String authKey) {
        this.email = email;
        this.Auth = authKey;
        this.expiration = 60;
    }

    public void Update(String auth){
        this.Auth = auth;
    }
}
