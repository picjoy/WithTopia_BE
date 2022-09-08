package com.four.withtopia.dto.response;

import com.four.withtopia.db.domain.Member;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MemberResponseDto {
  private Long id;
  private String nickname;
  private String email;
  private String ProfileImage;

  public static MemberResponseDto createMemberResponseDto(Member member){
    return MemberResponseDto.builder()
            .id(member.getMemberId())
            .nickname(member.getNickName())
            .email(member.getEmail())
            .ProfileImage(member.getProfileImage())
            .build();
  }
  public static MemberResponseDto createSocialMemberResponseDto(Member member){

    String nickname[] = member.getNickName().split("_");

    return MemberResponseDto.builder()
            .id(member.getMemberId())
            .nickname(nickname[0])
            .email(member.getEmail())
            .ProfileImage(member.getProfileImage())
            .build();
  }
}
