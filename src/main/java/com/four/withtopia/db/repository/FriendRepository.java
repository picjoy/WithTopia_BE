package com.four.withtopia.db.repository;

import com.four.withtopia.db.domain.Friend;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FriendRepository extends JpaRepository<Friend,String> {

    List<Friend>  findAllByMyNickname(String myNickname);

    Page<Friend> findByMyNicknameOrderByCreatedAt(String myNickname, Pageable pageable);

    Optional<Friend> findByMyNicknameAndFriendNickname(String myNickname, String friendNickname);
}
