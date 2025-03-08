package com.example.community.entity;

import jakarta.persistence.*;
import lombok.*;


@Entity
@Table(name = "post")  // 테이블명 명시
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Post extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(columnDefinition = "BIGINT UNSIGNED")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;  // FK (user 테이블의 id 참조)

    @Column(nullable = false, length = 255)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(columnDefinition = "TEXT")
    private String imageUrl;

    @Column(nullable = false, columnDefinition = "INT DEFAULT 0")
    private int viewCount;

    @Column(nullable = false, columnDefinition = "INT DEFAULT 0 COMMENT '캐싱'")
    private int likeCount;

    @Column(nullable = false, columnDefinition = "INT DEFAULT 0 COMMENT '캐싱'")
    private int commentCount;

    public void update(String title, String content, String imageUrl) {
        this.title = title;
        this.content = content;
        this.imageUrl = imageUrl;
    }
}
