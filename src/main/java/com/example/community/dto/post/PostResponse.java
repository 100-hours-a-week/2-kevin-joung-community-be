package com.example.community.dto.post;

import com.example.community.dto.user.UserResponse;
import com.example.community.entity.Post;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Objects;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostResponse {
    private Long id;
    private String title;
    private String content;
    private String imageUrl;
    private int likeCount;
    private int commentCount;
    private int viewCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Boolean isMine;
    private UserResponse user;

    public static PostResponse fromEntity(Post post, Long currentUserId) {
        return PostResponse.builder()
                .id(post.getId())
                .title(post.getTitle())
                .likeCount(post.getLikeCount())
                .commentCount(post.getCommentCount())
                .viewCount(post.getViewCount())
                .createdAt(post.getCreatedAt())
                .updatedAt(post.getUpdatedAt())
                .content(post.getContent())
                .imageUrl(post.getImageUrl())
                .isMine(Objects.equals(post.getUser().getId(), currentUserId))
                .user(UserResponse.fromEntity(post.getUser()))
                .build();
    }
}

