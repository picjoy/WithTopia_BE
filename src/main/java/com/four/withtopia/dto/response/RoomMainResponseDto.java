package com.four.withtopia.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Builder
@Getter
public class RoomMainResponseDto {
    private String sessionId;
    private String roomTitle;
    private Long cntMember;     // 몇 있을거야?
    private Long maxMember;
    private String masterId;    // 방장
    private List<RoomMemberResponseDto> roomMemberResponseDtoList;  // 멤버 리스트
    private boolean status;

}
