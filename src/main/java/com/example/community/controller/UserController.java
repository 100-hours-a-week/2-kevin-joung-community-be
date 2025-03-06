package com.example.community.controller;

import com.example.community.dto.BaseResponse;
import com.example.community.dto.user.*;
import com.example.community.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    // 회원가입 API
    @PostMapping("/")
    public ResponseEntity<BaseResponse<Void>> register(@RequestBody UserRegisterRequest request) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(userService.register(request));
    }

    // 로그인 API
    @PostMapping("/token")
    public ResponseEntity<BaseResponse<TokenResponse>> login(@RequestBody UserLoginRequest request) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(userService.login(request));
    }

    // 토큰 재발급 API
    @PutMapping("/token")
    public ResponseEntity<BaseResponse<TokenResponse>> refreshAccessToken(@RequestBody UserRefreshTokenRequest request) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(userService.refreshAccessToken(request));
    }

    // 내 정보 조회 API
    @GetMapping("/me")
    public ResponseEntity<BaseResponse<UserResponse>> getMyInfo() {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(userService.getMyInfo());
    }

    // 회원 정보 수정 API
    @PutMapping("/me")
    public ResponseEntity<BaseResponse<Void>> updateUserInfo(@RequestBody UserUpdateInfoRequest request) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(userService.updateUserInfo(request));
    }

    // 비밀번호 변경 API
    @PutMapping("/me/password")
    public ResponseEntity<BaseResponse<Void>> updatePassword(@RequestBody UserUpdatePasswordRequest request) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(userService.updatePassword(request));
    }
}
