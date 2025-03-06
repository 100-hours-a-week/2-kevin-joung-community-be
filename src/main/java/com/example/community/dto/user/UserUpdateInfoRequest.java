package com.example.community.dto.user;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UserUpdateInfoRequest {
    private String nickname;
    private String profileImageUrl;
}
