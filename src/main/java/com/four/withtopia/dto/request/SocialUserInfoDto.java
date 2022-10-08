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
public class SocialUserInfoDto {
    private String kakaoId;
    private String googleId;
    private String nickName;
    private String email;

    public static SocialUserInfoDto createGoogleUserInfoDto(JsonNode jsonNode){
        return SocialUserInfoDto.builder()
                .kakaoId(null)
                .googleId(jsonNode.get("id").asText())
                .nickName(jsonNode.get("name").asText())
                .email(jsonNode.get("email").asText())
                .build();
    }

    public static SocialUserInfoDto createKakaoUserInfo(JsonNode jsonNode){
        if(jsonNode.get("kakao_account").get("email") == null){
            return SocialUserInfoDto.builder()
                    .kakaoId(jsonNode.get("id").asText())
                    .googleId(null)
                    .nickName(jsonNode.get("properties").get("nickname").asText())
                    .email(null)
                    .build();
        }
        return SocialUserInfoDto.builder()
                .kakaoId(jsonNode.get("id").asText())
                .googleId(null)
                .nickName(jsonNode.get("properties").get("nickname").asText())
                .email(jsonNode.get("kakao_account").get("email").asText())
                .build();
    }

}
