package com.four.withtopia.db.repository;

import com.four.withtopia.db.domain.Room;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoomRepository extends JpaRepository<Room, String> {
    void delete(Room room);

    //List<Room> findAllBySessionId(String sessionId);

    Page<Room> findAll(Pageable pageable);

    Optional<Room> findBySessionId(String sessionId);
}
