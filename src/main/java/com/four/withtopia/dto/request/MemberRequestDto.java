package com.four.withtopia.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MemberRequestDto {

  private String nickname;

  private String email;

  private String password;

  private String passwordConfirm;
  private String authKey;


}
