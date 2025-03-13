package com.example.community.controller;

import com.example.community.dto.BaseResponse;
import com.example.community.dto.post.PostDetailResponse;
import com.example.community.dto.post.PostFormRequest;
import com.example.community.dto.post.PostListResponse;
import com.example.community.dto.post.PostResponse;
import com.example.community.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class PostController {
    private final PostService postService;

    // 게시물 조회 API
    @GetMapping("")
    public ResponseEntity<BaseResponse<PostListResponse>> getPosts(
            @RequestParam(required = false) Long cursor,
            @RequestParam(defaultValue = "10") int limit
    ) {
        BaseResponse<PostListResponse> response = BaseResponse.of(
                "조회 성공",
                postService.getPosts(cursor, limit)
        );
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);
    }

    // 게시물 단건 조회 API
    @GetMapping("/{id}")
    public ResponseEntity<BaseResponse<PostDetailResponse>> getPost(@PathVariable Long id) {
        BaseResponse<PostDetailResponse> response = BaseResponse.of(
                "조회 성공",
                postService.getPost(id)
        );

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);
    }

    // 게시물 작성 API
    @PostMapping("")
    public ResponseEntity<BaseResponse<Void>> createPost(@RequestBody PostFormRequest request) {
        postService.createPost(request);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(BaseResponse.of("게시물 작성 성공"));
    }

    // 게시물 수정 API
    @PutMapping("/{id}")
    public ResponseEntity<BaseResponse<Void>> updatePost(
            @PathVariable Long id,
            @RequestBody PostFormRequest request
    ) {
        postService.updatePost(id, request);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(BaseResponse.of("게시물 수정 성공"));
    }

    // 게시글 삭제 API
    @DeleteMapping("/{id}")
    public ResponseEntity<BaseResponse<Void>> deletePost(@PathVariable Long id) {
        postService.deletePost(id);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(BaseResponse.of("게시물 삭제 성공"));
    }

    // 게시글 좋아요 토글 API
    @PostMapping("/{id}/likes")
    public ResponseEntity<BaseResponse<Boolean>> likePost(@PathVariable Long id) {
        Boolean result = postService.toggleLike(id);
        BaseResponse<Boolean> response = BaseResponse.of(
                result ? "좋아요 누르기 성공": "좋아요 취소 성공",
                result
        );
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);
    }

}
