package com.example.community.service;

import com.example.community.dto.user.*;
import com.example.community.entity.User;
import com.example.community.exception.APIException;
import com.example.community.exception.ErrorCode;
import com.example.community.repository.UserRepository;
import com.example.community.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.example.community.security.JwtUtil.getCurrentUser;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    // 회원가입
    public void register(UserRegisterRequest request) {
        // 요첨 값 검증
        validateEmail(request.getEmail());
        validateNickname(request.getNickname());
        validatePassword(request.getPassword());

        // 이메일 중복
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new APIException(ErrorCode.DUPLICATE_EMAIL);
        }

        // 닉네임 중복
        if (userRepository.findByNickname(request.getNickname()).isPresent()) {
            throw new APIException(ErrorCode.DUPLICATE_NICKNAME);
        }

        String encodedPassword = passwordEncoder.encode(request.getPassword());
        User newUser = User.builder()
                .email(request.getEmail())
                .password(encodedPassword)
                .nickname(request.getNickname())
                .profileImageUrl(request.getProfileImageUrl() != null ? request.getProfileImageUrl() : "")
                .build();

        userRepository.save(newUser);
    }

    // 로그인 (Access Token, Refresh Token 발급)
    public TokenResponse login(UserLoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new APIException(ErrorCode.WRONG_LOGIN_INFO)); // 이메일이 없다면

        // 비밀번호가 틀렸다면
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new APIException(ErrorCode.WRONG_LOGIN_INFO);
        }

        return TokenResponse.builder()
                .accessToken(jwtUtil.generateAccessToken(user.getId()))
                .refreshToken(jwtUtil.generateRefreshToken(user.getId()))
                .build();
    }

    // 토큰 재발급 (Access Token, Refresh Token 재발급)
    public TokenResponse refreshAccessToken(UserRefreshTokenRequest request) {
        // 리프레시 토큰 만료됐는지 검증
        if (!jwtUtil.validateToken(request.getRefreshToken())) {
            throw new APIException(ErrorCode.INVALID_TOKEN);
        }

        Long userId = jwtUtil.extractUserId(request.getRefreshToken());
        // 유저아이디 추출이 실패했다면
        if (userId == null) {
            throw new APIException(ErrorCode.INVALID_TOKEN);
        }

        return TokenResponse.builder()
                .accessToken(jwtUtil.generateAccessToken(userId))
                .refreshToken(jwtUtil.generateRefreshToken(userId))
                .build();
    }

    // 내 정보 조회
    public UserResponse getMyInfo() {
        User user = getCurrentUser();
        return UserResponse.fromEntity(user);
    }

    // 회원 정보 수정 (닉네임, 프로필 이미지 변경)
    public void updateUserInfo(UserUpdateInfoRequest request) {
        User user = getCurrentUser();
        String nickname = request.getNickname();
        String profileImageUrl = request.getProfileImageUrl();

        // 닉네임 변경 시 중복 검사
        if (nickname != null && !nickname.equals(user.getNickname())) {
            validateNickname(nickname);

            if (userRepository.findByNickname(nickname).isPresent()) {
                throw new APIException(ErrorCode.DUPLICATE_NICKNAME);
            }
        }

        user = User.builder()
                .id(user.getId())
                .email(user.getEmail())
                .password(user.getPassword())
                .nickname(nickname != null ? nickname : user.getNickname())
                .profileImageUrl(profileImageUrl != null ? profileImageUrl : user.getProfileImageUrl())
                .build();

        userRepository.save(user);
    }

    // 비밀번호 변경
    public void updatePassword(UserUpdatePasswordRequest request) {
        User user = getCurrentUser();

        validatePassword(request.getPassword());

        user = User.builder()
                .id(user.getId())
                .email(user.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .nickname(user.getNickname())
                .profileImageUrl(user.getProfileImageUrl())
                .build();

        userRepository.save(user);
    }

    // 이메일 검증
    private void validateEmail(String email) {
        if (email == null || !email.contains("@")) {
            throw new APIException(ErrorCode.INVALID_EMAIL);
        }
    }

    // 닉네임 검증
    private void validateNickname(String nickname) {
        if (nickname == null ||
                nickname.isEmpty() ||
                nickname.contains(" ") ||
                nickname.length() > 10
        ) {
            throw new APIException(ErrorCode.INVALID_NICKNAME);
        }
    }

    // 비밀번호 검증
    private void validatePassword(String password) {
        if (password == null || password.length() < 8 || password.length() > 20) {
            throw new APIException(ErrorCode.INVALID_PASSWORD);
        }

        if (!password.matches(".*[A-Z].*")) { // 대문자 포함
            throw new APIException(ErrorCode.INVALID_PASSWORD);
        }

        if (!password.matches(".*[a-z].*")) { // 소문자 포함
            throw new APIException(ErrorCode.INVALID_PASSWORD);
        }

        if (!password.matches(".*[!@#$%^&*(),.?\":{}|<>].*")) { // 특수문자 포함
            throw new APIException(ErrorCode.INVALID_PASSWORD);
        }
    }

}
