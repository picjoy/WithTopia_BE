package com.four.withtopia.dto.response;

import com.four.withtopia.db.domain.Member;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MemberResponseDto {
  private Long id;
  private String nickname;
  private LocalDateTime createdAt;
  private LocalDateTime modifiedAt;

  public static MemberResponseDto createMemberResponseDto(Member member){
    return MemberResponseDto.builder()
            .id(member.getMemberId())
            .nickname(member.getNickName())
            .createdAt(member.getCreatedAt())
            .modifiedAt(member.getModifiedAt())
            .build();
  }
}
