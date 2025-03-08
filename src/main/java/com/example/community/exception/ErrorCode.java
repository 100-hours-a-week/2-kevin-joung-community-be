package com.example.community.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {
    // common error
    INVALID_REQUEST(HttpStatus.BAD_REQUEST, "유효하지 않은 요청입니다."),
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "유효하지 않은 토큰입니다."),
    SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버 에러"),

    // user
    DUPLICATE_EMAIL(HttpStatus.CONFLICT, "중복된 이메일입니다."),
    DUPLICATE_NICKNAME(HttpStatus.CONFLICT, "중복된 닉네임입니다."),
    INVALID_EMAIL(HttpStatus.BAD_REQUEST, "잘못된 이메일 형식입니다."),
    INVALID_NICKNAME(HttpStatus.BAD_REQUEST, "잘못된 닉네임 형식입니다."),
    INVALID_PASSWORD(HttpStatus.BAD_REQUEST, "잘못된 패스워드 형식입니다."),
    WRONG_LOGIN_INFO(HttpStatus.UNAUTHORIZED, "이메일 또는 비밀번호가 잘못되었습니다."),

    // post
    POST_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 게시글이 없습니다."),
    FORBIDDEN_POST_ACCESS(HttpStatus.FORBIDDEN, "본인의 게시글만 수정/삭제할 수 있습니다.")


    ;
    private final HttpStatus status;
    private final String message;

    ErrorCode(HttpStatus status, String message) {
        this.status = status;
        this.message = message;
    }
}
