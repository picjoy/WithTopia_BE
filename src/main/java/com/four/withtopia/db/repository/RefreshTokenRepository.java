package com.four.withtopia.db.repository;


import com.four.withtopia.db.domain.Member;
import com.four.withtopia.db.domain.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByMember(Member member);
}
