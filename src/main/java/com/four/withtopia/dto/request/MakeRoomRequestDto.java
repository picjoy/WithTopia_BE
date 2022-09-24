package com.four.withtopia.dto.request;

import lombok.*;

import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Getter
@RequiredArgsConstructor
public class MakeRoomRequestDto {
    private String roomTitle;   // 방제
    private Long maxMember;      // 방 인원 설정
    private boolean status;      // 방 상태(public / private)

    @Size(min = 4, max = 12)
    @Pattern(regexp = "[a-zA-Z\\d!@#$%^&*]*${4,12}")    // 영대소문자, 숫자, 최소 4자리에서 12자리
    private String password;    // 방이 private으로 설정될 시 패스워드 입력
}
