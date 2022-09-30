package com.four.withtopia.dto.request;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.*;

@Getter
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
public class KakaoUserInfoDto {
    private String kakaoId;
    private String nickName;
    private String email;

    public static KakaoUserInfoDto createKakaoUserInfo(JsonNode jsonNode){
        if(jsonNode.get("kakao_account").get("email") == null){
            return KakaoUserInfoDto.builder()
                    .kakaoId(jsonNode.get("id").asText())
                    .nickName(jsonNode.get("properties").get("nickname").asText())
                    .email(null)
                    .build();
        }
        return KakaoUserInfoDto.builder()
                .kakaoId(jsonNode.get("id").asText())
                .nickName(jsonNode.get("properties").get("nickname").asText())
                .email(jsonNode.get("kakao_account").get("email").asText())
                .build();
    }
}
