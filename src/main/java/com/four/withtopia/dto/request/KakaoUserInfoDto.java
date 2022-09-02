package com.four.withtopia.dto.request;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class KakaoUserInfoDto {
    private String kakaoId;
    private String nickName;
    private String email;

    public static KakaoUserInfoDto createKakaoUserInfo(JsonNode jsonNode){
        return KakaoUserInfoDto.builder()
                .kakaoId(jsonNode.get("id").asText())
                .nickName(jsonNode.get("properties").get("nickname").asText())
                .email(jsonNode.get("kakao_account").get("email").asText())
                .build();
    }
}
