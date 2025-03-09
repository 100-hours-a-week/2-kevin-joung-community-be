package com.example.community.dto.comment;

import com.example.community.dto.user.UserResponse;
import com.example.community.entity.PostComment;
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
public class CommentResponse {
    private Long id;
    private String content;
    private Boolean isMine;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private UserResponse user;

    public static CommentResponse fromEntity(PostComment comment, Long currentUserId) {
         return CommentResponse.builder()
                 .id(comment.getId())
                 .content(comment.getContent())
                 .isMine(Objects.equals(comment.getUser().getId(), currentUserId))
                 .createdAt(comment.getCreatedAt())
                 .updatedAt(comment.getUpdatedAt())
                 .user(UserResponse.fromEntity(comment.getUser()))
                 .build();
    }
}
