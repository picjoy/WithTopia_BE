package com.four.withtopia.db.repository;


import com.four.withtopia.db.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {

    Optional<Member> findByMemberId(Long id);
    Optional<Member> findByEmail(String email);
    Optional<Member> findByNickName(String nickname);

    boolean existsByEmail(String email);

    boolean existsByNickName(String nickname);


    Member findByKakaoId(String kakaoId);


}
