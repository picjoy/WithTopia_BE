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
public class MypageResponseDto {
    private Long memberId;
    private String nickName;
    private String email;
    private String profileImage;
    private long lickCnt;

    public static MypageResponseDto createMypageResponseDto(Member member){
        return MypageResponseDto.builder()
                .memberId(member.getMemberId())
                .nickName(member.getNickName())
                .email(member.getEmail())
                .profileImage(member.getProfileImage())
                .lickCnt(member.getLikeCount())
                .build();
    }
}
