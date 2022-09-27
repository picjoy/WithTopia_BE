package com.four.withtopia.db.domain;

import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoomMember {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long roomMemberId;

    // 채팅방
    @Column
    private String sessionId;

    // MemberEntityId
    @Column
    private Long member;

    @Column
    private String nickname;

    @Column
    private String email;

    @Column
    private String ProfileImage;

    @Column
    private String enterRoomToken;


}
