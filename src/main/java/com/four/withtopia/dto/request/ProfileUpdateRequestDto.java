package com.four.withtopia.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ProfileUpdateRequestDto {
    private String nickName;
    private String profileImage;

    public void nicknameUpdateRequestDto(String nickName) {
        this.nickName = nickName;
    }

}
