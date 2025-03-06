package com.example.community.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import java.util.Optional;

@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BaseResponse<T> {
    private final String message;
    private final T data;

    // 데이터가 없는 응답
    public static <T> BaseResponse<T> of(String message) {
        return new BaseResponse<>(message, null);
    }

    // 데이터가 있는 응답
    public static <T> BaseResponse<T> of(String message, T data) {
        return new BaseResponse<>(message, data);
    }

    private BaseResponse(String message, T data) {
        this.message = message;
        this.data = data;
    }
}

