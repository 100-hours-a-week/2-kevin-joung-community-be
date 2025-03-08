package com.example.community.repository;

import com.example.community.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {

    // 커서 페이지네이션으로 post 가져오기
    @Query("""
             SELECT p FROM Post p WHERE (:cursor IS NULL OR p.id < :cursor)
             ORDER BY p.id DESC LIMIT :limit
            """)
    List<Post> findPostsByCursor(@Param("cursor") Long cursor, @Param("limit") int limit);

}
