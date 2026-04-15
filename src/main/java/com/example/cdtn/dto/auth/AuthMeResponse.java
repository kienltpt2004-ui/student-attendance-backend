package com.example.cdtn.dto.auth;

import com.example.cdtn.entity.enums.Role;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class AuthMeResponse {
    private Long id;
    private String email;
    private Role role;
}
