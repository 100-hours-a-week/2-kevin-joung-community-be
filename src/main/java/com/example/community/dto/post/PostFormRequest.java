package com.example.community.dto.post;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PostFormRequest {
    private String title;
    private String content;
    private String imageUrl;
}
