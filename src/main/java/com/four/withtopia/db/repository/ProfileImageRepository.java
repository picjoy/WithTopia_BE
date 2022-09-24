package com.four.withtopia.db.repository;

import com.four.withtopia.db.domain.ProfileImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProfileImageRepository extends JpaRepository<ProfileImage, Long> {
    Optional<ProfileImage> findById(Long id);
    List<ProfileImage> findAll();
}
