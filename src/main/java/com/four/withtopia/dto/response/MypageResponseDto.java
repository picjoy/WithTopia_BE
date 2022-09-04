package com.four.withtopia.dto.response;

import com.four.withtopia.db.domain.Member;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
public class MypageResponseDto {
    private Long id;
    private String nickname;
    private String email;
    private String profileImage;

    public static MypageResponseDto createMypageResponseDto(Member member){
        return MypageResponseDto.builder()
                .id(member.getMemberId())
                .nickname(member.getNickName())
                .email(member.getEmail())
                .profileImage(member.getProfileImage())
                .build();
    }
}
