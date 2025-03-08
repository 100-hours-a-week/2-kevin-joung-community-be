package com.example.community.service;

import com.example.community.dto.BaseResponse;
import com.example.community.dto.PostListResponse;
import com.example.community.dto.PostResponse;
import com.example.community.entity.Post;
import com.example.community.entity.User;
import com.example.community.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class PostService {
    private final PostRepository postRepository;

    // 현재 로그인된 사용자 정보 가져오기
    private User getCurrentUser() {
        return (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    // 게시글 목록 조회
    public BaseResponse<PostListResponse> getPosts(Long cursor, int limit) {
        User user = getCurrentUser();
        List<Post> posts = postRepository.findPostsByCursor(cursor, limit);
        List<PostResponse> postResponseList = posts.stream()
                .map(post -> PostResponse.fromEntity(post, user.getId()))
                .toList();

        PostListResponse response = PostListResponse.builder()
                .posts(postResponseList)
                .build();
        return BaseResponse.of(
                "조회 성공",
                response
        );

    }

}
