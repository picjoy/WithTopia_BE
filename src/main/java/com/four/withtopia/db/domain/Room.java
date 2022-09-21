package com.four.withtopia.db.domain;

import com.four.withtopia.util.Timestamped;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;

@Getter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Room extends Timestamped {

    @Id
    private String sessionId;      // 방 번호
                                // Openvidu에서 발급된 해당 채팅방에 입장하기 위한 세션 (세션 == 채팅방)
                                // 다른 유저들이 해당 채팅방에 입장 요청시 해당 컬럼을 사용하여 오픈비두에 다른 유저들의 채팅방 입장을 위한 토큰을 생성합니다.
    @Column(nullable = false)
    private String roomTitle;       // 방제

    @Column
    private Long maxMember;          // 전체 방 인원

    @Column
    private String masterId;         // 방 생성자(방장)

    @Column
    private boolean status;      // 방 상태(public / private)

    @OneToMany(mappedBy = "sessionId", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RoomMember> roomMembers;

    @Column
    private Long cntMember;         // 현재 방 인원

    public void rename(String updateRoomTitle){
        this.roomTitle = updateRoomTitle;
    }

    public void updateCntMember(Long cntMember) {
        this.cntMember = cntMember;
    }

    public boolean validateMember(Member member) {
        System.out.println(member.getNickName());
        System.out.println(this.masterId);
        return !this.masterId.equals(member.getNickName());
    }

}
