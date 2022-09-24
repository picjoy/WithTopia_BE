package com.four.withtopia.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class ProfileImageListResponseDto {
    private Long imageId;
    private String imageUrl;
}
