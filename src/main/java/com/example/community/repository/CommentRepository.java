package com.example.community.repository;

import com.example.community.entity.Post;
import com.example.community.entity.PostComment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CommentRepository extends JpaRepository<PostComment, Long> {
    // 커서 페이지네이션으로 comment 가져오기
    @Query("""
             SELECT c FROM PostComment c
             WHERE c.post.id = :postId
             AND (:cursor IS NULL OR c.id < :cursor)
             ORDER BY c.id DESC LIMIT :limit
            """)
    List<PostComment> findCommentsByCursor(@Param("postId") Long postId, @Param("cursor") Long cursor, @Param("limit") int limit);

    Long id(Long id);
}
