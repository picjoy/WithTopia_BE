package com.four.withtopia.db.domain;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;


@NoArgsConstructor
@Builder
@AllArgsConstructor
@Getter
@Setter
@RedisHash(value = "auth",timeToLive = 30)
public class EmailAuth {

    @Id
    private String authId;

    @Indexed
    private String email;

    @Indexed
    private String Auth;

    public EmailAuth(String email, String authKey) {
        this.email = email;
        this.Auth = authKey;
    }

    public void Update(String auth){
        this.Auth = auth;
    }
}
