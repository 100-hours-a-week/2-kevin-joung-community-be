package com.example.community.exception;

import com.example.community.dto.BaseResponse;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // API 에러 처리
    @ExceptionHandler(APIException.class)
    public ResponseEntity<BaseResponse<Void>> handleCustomException(APIException e) {
        return ResponseEntity.status(e.getErrorCode().getStatus())
                .body(BaseResponse.of(e.getErrorCode().getMessage()));
    }

    // 서버 내부 오류(500)
    @ExceptionHandler(Exception.class)
    public ResponseEntity<BaseResponse<Void>> handleServerError(Exception e) {
        System.out.println("‼️에러 발생: " + e.getMessage());
        return ResponseEntity.status(ErrorCode.SERVER_ERROR.getStatus())
                .body(BaseResponse.of(ErrorCode.SERVER_ERROR.getMessage()));
    }
}
