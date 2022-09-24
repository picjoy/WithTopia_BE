package com.four.withtopia.db.repository;

import com.four.withtopia.db.domain.Room;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoomRepository extends JpaRepository<Room, String> {
    void delete(Room room);


    Page<Room> findAllByOrderByModifiedAtAsc(Pageable pageable);



    // Containing을 붙여주면 Like 검색이 가능
    // %{keyword}%가 가능
    Page<Room> findByRoomTitleContainingOrderByModifiedAtAsc(String keyword, Pageable pageable);
}
