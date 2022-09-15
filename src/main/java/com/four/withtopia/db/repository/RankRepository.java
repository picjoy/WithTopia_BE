package com.four.withtopia.db.repository;

import com.four.withtopia.db.domain.Rank;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RankRepository extends JpaRepository<Rank, Long> {
    Rank findByNickname(String nickname);
}
