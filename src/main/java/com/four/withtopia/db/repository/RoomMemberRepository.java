package com.four.withtopia.db.repository;

import com.four.withtopia.db.domain.RoomMember;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RoomMemberRepository extends JpaRepository<RoomMember, Long> {

    Long countAllBySessionId(String SessionId);


//    RoomMember findByMemberId(Long memberId);

    List<RoomMember> findAllBySessionId(String sessionId);
//    int countAllByRoomId(String roomId);



}
