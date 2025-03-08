package com.example.community.controller;

import com.example.community.dto.BaseResponse;
import com.example.community.dto.PostListResponse;
import com.example.community.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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

}
