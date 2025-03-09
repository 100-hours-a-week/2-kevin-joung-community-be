package com.example.community.controller;

import com.example.community.dto.BaseResponse;
import com.example.community.dto.comment.CommentFormRequest;
import com.example.community.dto.comment.CommentListResponse;
import com.example.community.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/posts/{postId}/comments")
@RequiredArgsConstructor
public class CommentController {
    private final CommentService commentService;

    // 댓글 목록 조회 API
    @GetMapping("")
    public ResponseEntity<BaseResponse<CommentListResponse>> getComments(
            @PathVariable Long postId,
            @RequestParam(required = false) Long cursor,
            @RequestParam(defaultValue = "10") int limit
    ) {
        BaseResponse<CommentListResponse> response = BaseResponse.of(
                "조회 성공",
                commentService.getComments(postId, cursor, limit)
        );

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);
    }


    // 댓글 작성 API
    @PostMapping("")
    public ResponseEntity<BaseResponse<Void>> createComment(
            @PathVariable Long postId,
            @RequestBody CommentFormRequest request
    ) {
        commentService.createComment(postId, request);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(BaseResponse.of("댓글 작성 성공"));
    }

    // 댓글 수정 API
    @PutMapping("/{commentId}")
    public ResponseEntity<BaseResponse<Void>> updateComment(
            @PathVariable Long commentId,
            @RequestBody CommentFormRequest request
    ) {
        commentService.updateComment(commentId, request);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(BaseResponse.of("댓글 수정 성공"));
    }

    // 댓글 삭제 API
    @DeleteMapping("/{commentId}")
    public ResponseEntity<BaseResponse<Void>> deleteComment(
            @PathVariable Long postId,
            @PathVariable Long commentId
    ) {
        commentService.deleteComment(postId, commentId);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(BaseResponse.of("댓글 삭제 성공"));
    }

}