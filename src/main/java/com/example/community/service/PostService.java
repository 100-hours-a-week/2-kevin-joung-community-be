package com.example.community.service;

import com.example.community.dto.post.PostFormRequest;
import com.example.community.dto.post.PostListResponse;
import com.example.community.dto.post.PostResponse;
import com.example.community.entity.Post;
import com.example.community.entity.PostLike;
import com.example.community.entity.User;
import com.example.community.exception.APIException;
import com.example.community.exception.ErrorCode;
import com.example.community.repository.LikeRepository;
import com.example.community.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static com.example.community.security.JwtUtil.getCurrentUser;

@Service
@RequiredArgsConstructor
@Transactional
public class PostService {
    private final PostRepository postRepository;
    private final LikeRepository likeRepository;

    // 게시글 목록 조회
    public PostListResponse getPosts(Long cursor, int limit) {
        User user = getCurrentUser();
        List<Post> posts = postRepository.findPostsByCursor(cursor, limit);
        List<PostResponse> postResponseList = posts.stream()
                .map(post -> PostResponse.fromEntity(post, user.getId()))
                .toList();

        return PostListResponse.builder()
                .posts(postResponseList)
                .build();
    }

    // 게시글 조회
    public PostResponse getPost(Long id) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new APIException(ErrorCode.POST_NOT_FOUND));

        post.incViewCount();
        postRepository.save(post);

        return PostResponse.fromEntity(
                post,
                getCurrentUser().getId()
        );
    }

    // 게시글 작성
    public void createPost(PostFormRequest request) {
        User user = getCurrentUser();
        Post newPost = Post.builder()
                .user(user)
                .title(request.getTitle())
                .content(request.getContent())
                .imageUrl(request.getImageUrl())
                .build();
        postRepository.save(newPost);
    }

    // 게시글 수정
    public void updatePost(Long id, PostFormRequest request) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new APIException(ErrorCode.POST_NOT_FOUND));

        // 자신의 게시글만 수정 가능
        User user = getCurrentUser();
        if (!Objects.equals(user.getId(), post.getUser().getId())) {
            throw new APIException(ErrorCode.FORBIDDEN_POST_ACCESS);
        }

        post.update(request.getTitle(), request.getContent(), request.getImageUrl());
        postRepository.save(post);
    }

    // 게시글 삭제
    public void deletePost(Long id) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new APIException(ErrorCode.POST_NOT_FOUND));

        // 자신의 게시글만 삭제 가능
        User user = getCurrentUser();
        if (!Objects.equals(user.getId(), post.getUser().getId())) {
            throw new APIException(ErrorCode.FORBIDDEN_POST_ACCESS);
        }

        postRepository.deleteById(id);
    }

    // 게시글 좋아요 토글
    public Boolean toggleLike(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new APIException(ErrorCode.POST_NOT_FOUND));

        User user = getCurrentUser();

        Optional<PostLike> existingLike = likeRepository.findByPostAndUser(post, user);

        if (existingLike.isPresent()) {
            likeRepository.delete(existingLike.get());
            post.decreaseLikeCount();
            postRepository.save(post);
            return false;
        } else {
            PostLike postLike = PostLike.builder()
                    .post(post)
                    .user(user)
                    .build();

            likeRepository.save(postLike);
            post.increaseLikeCount();
            postRepository.save(post);
            return true;
        }
    }
}
