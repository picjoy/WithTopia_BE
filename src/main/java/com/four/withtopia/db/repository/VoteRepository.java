package com.four.withtopia.db.repository;

import com.four.withtopia.db.domain.Vote;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VoteRepository extends JpaRepository<Vote, Long> {
    Vote findByVoteByAndVoteTo(String voteBy, String voteTo);
}
