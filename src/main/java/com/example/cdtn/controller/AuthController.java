package com.example.cdtn.controller;

import com.example.cdtn.dto.auth.AuthMeResponse;
import com.example.cdtn.dto.auth.LoginRequest;
import com.example.cdtn.dto.auth.LoginResponse;
import com.example.cdtn.dto.response.ApiResponse;
import com.example.cdtn.entity.enums.Status;
import com.example.cdtn.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(
                new ApiResponse<>(
                    "Đăng nhập thành công",
                    Status.SUCCESS,
                    "",
                    authService.login(request)
                )
        );
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<AuthMeResponse>> me() {
        return ResponseEntity.ok(
                new ApiResponse<>(
                        "Lấy thông tin người dùng thành công",
                        Status.SUCCESS,
                        "",
                        authService.me()
                )
        );
    }
}