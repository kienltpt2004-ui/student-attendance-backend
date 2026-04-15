package com.example.cdtn.exception;

import com.example.cdtn.dto.response.ApiResponse;
import com.example.cdtn.entity.enums.Status;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    //VALIDATION ERROR
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleValidation(MethodArgumentNotValidException ex) {

        Map<String, String> errors = new HashMap<>();

        ex.getBindingResult().getFieldErrors()
                .forEach(err -> errors.put(err.getField(), err.getDefaultMessage()));

        return ResponseEntity.badRequest().body(
                new ApiResponse<>(
                        "Dữ liệu không hợp lệ",
                        Status.BAD_REQUEST,
                        "Validation failed",
                        errors
                )
        );
    }

    //NOT FOUND
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<?> handleNotFound(ResourceNotFoundException ex) {
        return ResponseEntity.status(404).body(
                new ApiResponse<>(
                        "Không tìm thấy dữ liệu",
                        Status.NOT_FOUND,
                        ex.getMessage(),
                        null
                )
        );
    }

    //BAD REQUEST
    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<?> handleBadRequest(BadRequestException ex) {
        return ResponseEntity.badRequest().body(
                new ApiResponse<>(
                        "Yêu cầu không hợp lệ",
                        Status.BAD_REQUEST,
                        ex.getMessage(),
                        null
                )
        );
    }

    //UNAUTHORIZED
    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<?> handleUnauthorized(UnauthorizedException ex) {
        return ResponseEntity.status(401).body(
                new ApiResponse<>(
                        "Chưa xác thực",
                        Status.UNAUTHORIZED,
                        ex.getMessage(),
                        null
                )
        );
    }

    //FORBIDDEN
    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<?> handleForbidden(ForbiddenException ex) {
        return ResponseEntity.status(403).body(
                new ApiResponse<>(
                        "Không có quyền truy cập",
                        Status.FORBIDDEN,
                        ex.getMessage(),
                        null
                )
        );
    }

    //SYSTEM ERROR
    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleSystem(Exception ex) {
        return ResponseEntity.status(500).body(
                new ApiResponse<>(
                        "Lỗi hệ thống",
                        Status.ERROR,
                        ex.getMessage(),
                        null
                )
        );
    }
}