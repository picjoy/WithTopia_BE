package com.four.withtopia.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class RoomCreateResponseDto {
    private String sessionId;
    private String roomTitle;
    private String masterId;
    private Long maxMember;
    private Long cntMember;
    private List<RoomMemberResponseDto> roomMemberResponseDtoList;  // 멤버 리스트
    private boolean status;
    private String token;
    private String password;
}
