package com.four.withtopia.db.repository;

import com.four.withtopia.db.domain.BenMember;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BenMemberRepository extends JpaRepository<BenMember, Long> {
    BenMember findByMemberIdAndRoomId(Long memberId, String sessionId);
}
