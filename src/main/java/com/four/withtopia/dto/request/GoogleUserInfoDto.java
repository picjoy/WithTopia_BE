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
public class GoogleUserInfoDto {
    private String googleId;
    private String nickName;
    private String email;

    public static GoogleUserInfoDto createGoogleUserInfoDto(JsonNode jsonNode){
        return GoogleUserInfoDto.builder()
                .googleId(jsonNode.get("id").asText())
                .nickName(jsonNode.get("name").asText())
                .email(jsonNode.get("email").asText())
                .build();
    }
}
