package com.example.community.service;

import com.example.community.dto.comment.CommentFormRequest;
import com.example.community.dto.comment.CommentListResponse;
import com.example.community.dto.comment.CommentResponse;
import com.example.community.entity.Post;
import com.example.community.entity.PostComment;
import com.example.community.entity.User;
import com.example.community.exception.APIException;
import com.example.community.exception.ErrorCode;
import com.example.community.repository.CommentRepository;
import com.example.community.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Transactional
public class CommentService {
    private final CommentRepository commentRepository;
    private final PostRepository postRepository;

    // 현재 로그인된 사용자 정보 가져오기
    private User getCurrentUser() {
        return (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    // 댓글 목록 조회
    public CommentListResponse getComments(Long postId, Long cursor, int limit) {
        User user = getCurrentUser();
        List<PostComment> comments = commentRepository.findCommentsByCursor(postId, cursor, limit);
        List<CommentResponse> commentResponseList = comments.stream()
                .map(comment -> CommentResponse.fromEntity(comment, user.getId()))
                .toList();

        return CommentListResponse.builder()
                .comments(commentResponseList)
                .build();
    }

    // 댓글 작성
    public void createComment(Long postId, CommentFormRequest request) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new APIException(ErrorCode.POST_NOT_FOUND));
        User user = getCurrentUser();

        PostComment newComment = PostComment.builder()
                .post(post)
                .user(user)
                .content(request.getContent())
                .build();
        commentRepository.save(newComment);

        // 댓글 작성 시 게시글 commentCount+
        post.increaseCommentCount();
        postRepository.save(post);
    }

    // 댓글 수정
    public void updateComment(Long id, CommentFormRequest request) {
        PostComment comment = commentRepository.findById(id)
                .orElseThrow(() -> new APIException(ErrorCode.COMMENT_NOT_FOUND));

        //자신의 댓글만 수정 가능
        User user = getCurrentUser();
        if (!Objects.equals(user.getId(), comment.getUser().getId())) {
            throw new APIException(ErrorCode.FORBIDDEN_COMMENT_ACCESS);
        }

        comment.update(request.getContent());
        commentRepository.save(comment);
    }

    // 댓글 삭제
    public void deleteComment(Long postId, Long commentId) {
        PostComment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new APIException(ErrorCode.COMMENT_NOT_FOUND));
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new APIException(ErrorCode.POST_NOT_FOUND));

        //자신의 댓글만 삭제 가능
        User user = getCurrentUser();
        if (!Objects.equals(user.getId(), comment.getUser().getId())) {
            throw new APIException(ErrorCode.FORBIDDEN_COMMENT_ACCESS);
        }

        commentRepository.delete(comment);

        // 댓글 삭제 시 게시글 commentCount-
        post.decreaseCommentCount();
        postRepository.save(post);
    }

}
