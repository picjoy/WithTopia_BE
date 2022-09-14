package com.four.withtopia.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class MakeRoomRequestDto {
    private String roomTitle;   // 방제
    private Long maxMember;      // 방 인원 설정
    private boolean status;      // 방 상태(public / private)
}
