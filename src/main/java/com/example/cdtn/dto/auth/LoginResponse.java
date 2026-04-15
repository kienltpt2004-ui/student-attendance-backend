package com.example.cdtn.dto.auth;
import com.example.cdtn.entity.enums.Role;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class LoginResponse {
    private String token;
    private Long userId;
    private String email;
    private Role role;
}
