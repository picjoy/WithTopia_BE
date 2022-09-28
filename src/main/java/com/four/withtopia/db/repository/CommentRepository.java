package com.four.withtopia.db.repository;

import com.four.withtopia.db.domain.Comment;
import com.four.withtopia.db.domain.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    Page<Comment> findAllByPostOrderByModifiedAtAsc(Post post, Pageable pageable);

}
