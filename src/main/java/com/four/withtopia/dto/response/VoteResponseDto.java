package com.four.withtopia.dto.response;

import com.four.withtopia.db.domain.Member;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VoteResponseDto {
    private String nickname;
    private Long likeCnt;

    public static VoteResponseDto createVoteResponse(Member rank){
        return VoteResponseDto.builder()
                .nickname(rank.getNickName())
                .likeCnt(rank.getLikeCount())
                .build();
    }
}
