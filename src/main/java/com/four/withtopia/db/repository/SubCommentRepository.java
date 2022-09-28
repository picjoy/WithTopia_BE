package com.four.withtopia.db.repository;

import com.four.withtopia.db.domain.SubComment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SubCommentRepository extends JpaRepository<SubComment, Long> {

    List<SubComment> findAllByCommentId(Long commentId);
}
