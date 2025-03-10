package com.example.community.controller;

import com.example.community.dto.BaseResponse;
import com.example.community.dto.user.*;
import com.example.community.security.JwtUtil;
import com.example.community.service.UserService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final JwtUtil jwtUtil;
    private final UserService userService;

    // 회원가입 API
    @PostMapping("")
    public ResponseEntity<BaseResponse<Void>> register(@RequestBody UserRegisterRequest request) {
        userService.register(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(BaseResponse.of("회원가입 성공"));
    }

    // 로그인 API
    @PostMapping("/token")
    public ResponseEntity<BaseResponse<Void>> login(@RequestBody UserLoginRequest request, HttpServletResponse response) {
        TokenResponse tokens = userService.login(request);

        jwtUtil.addTokenCookie(response,"accessToken", tokens.getAccessToken(), JwtUtil.getAccessExpirationTime());
        jwtUtil.addTokenCookie(response,"refreshToken", tokens.getRefreshToken(), JwtUtil.getRefreshExpirationTime());

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(BaseResponse.of("로그인 성공"));
    }

    // 로그아웃 API
    @DeleteMapping("/token")
    public ResponseEntity<BaseResponse<Void>> logout(HttpServletResponse response) {
        jwtUtil.deleteTokenCookie(response,"accessToken");
        jwtUtil.deleteTokenCookie(response, "refreshToken");

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(BaseResponse.of("로그아웃 성공"));
    }

    // 토큰 재발급 API
//    @PutMapping("/token")
//    public ResponseEntity<BaseResponse<TokenResponse>> refreshAccessToken(@RequestBody UserRefreshTokenRequest request) {
//        BaseResponse<TokenResponse> response = BaseResponse.of(
//                "토큰 재발급 성공",
//                userService.refreshAccessToken(request)
//        );
//        return ResponseEntity
//                .status(HttpStatus.OK)
//                .body(response);
//    }

    // 내 정보 조회 API
    @GetMapping("/me")
    public ResponseEntity<BaseResponse<UserResponse>> getMyInfo() {
        BaseResponse<UserResponse> response = BaseResponse.of(
                "내 정보 조회 성공",
                userService.getMyInfo()
        );
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);
    }

    // 회원 정보 수정 API
    @PutMapping("/me")
    public ResponseEntity<BaseResponse<Void>> updateUserInfo(@RequestBody UserUpdateInfoRequest request) {
        userService.updateUserInfo(request);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(BaseResponse.of("회원 정보 수정 성공"));
    }

    // 비밀번호 변경 API
    @PutMapping("/me/password")
    public ResponseEntity<BaseResponse<Void>> updatePassword(@RequestBody UserUpdatePasswordRequest request) {
        userService.updatePassword(request);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(BaseResponse.of( "비밀번호 변경 성공"));
    }
}
