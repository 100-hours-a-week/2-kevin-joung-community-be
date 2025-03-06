package com.example.community.dto.user;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UserRegisterRequest {
    private String email;
    private String password;
    private String nickname;
    private String profileImageUrl;
}