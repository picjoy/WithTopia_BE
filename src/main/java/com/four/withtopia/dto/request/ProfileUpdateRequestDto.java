package com.four.withtopia.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ProfileUpdateRequestDto {
    private String nickname;
    private String profileImage;
}
