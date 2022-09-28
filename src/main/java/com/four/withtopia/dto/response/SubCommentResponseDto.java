package com.four.withtopia.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SubCommentResponseDto {

    private Long id;
    private Long commentId;
    private String nickname;
    private String profileImage;
    private String content;
}
