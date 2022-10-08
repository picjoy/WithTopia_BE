package com.four.withtopia.dto.response;

import com.four.withtopia.db.domain.RoomMember;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoomMemberResponseDto {

    private Long roomMemberId;

    // 채팅방
    private String sessionId;

    // 멤버
    private Long member;

    private String nickname;

    private String email;

    private String ProfileImage;

    // 방장인지 확인
    private boolean roomMaster;

    private String enterRoomToken;

    public RoomMemberResponseDto(RoomMember entity, boolean a){
        this.roomMemberId = entity.getRoomMemberId();
        this.sessionId = entity.getSessionId();
        this.member = entity.getMember();
        this.nickname = entity.getNickname();
        this.email = entity.getEmail();
        this.ProfileImage = entity.getProfileImage();
        this.enterRoomToken = entity.getEnterRoomToken();
        this.roomMaster = a;
    }

    public RoomMemberResponseDto(RoomMember entity) {
        this.roomMemberId = entity.getRoomMemberId();
        this.sessionId = entity.getSessionId();
        this.member = entity.getMember();
        this.nickname = entity.getNickname();
        this.email = entity.getEmail();
        this.ProfileImage = entity.getProfileImage();
        this.enterRoomToken = entity.getEnterRoomToken();
        this.roomMaster = false;
    }
}
