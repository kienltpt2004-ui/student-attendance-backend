package com.example.cdtn.service;

import com.example.cdtn.dto.auth.AuthMeResponse;
import com.example.cdtn.dto.auth.LoginRequest;
import com.example.cdtn.dto.auth.LoginResponse;
import com.example.cdtn.entity.User;
import com.example.cdtn.exception.ResourceNotFoundException;
import com.example.cdtn.repository.UserRepository;
import com.example.cdtn.security.CustomUserDetails;
import com.example.cdtn.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final JwtService jwtService;

    public LoginResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy người dùng"));

        String token = jwtService.generateToken(userDetails);

        return LoginResponse.builder()
                .token(token)
                .userId(user.getId())
                .email(user.getEmail())
                .role(user.getRole())
                .build();
    }
    public AuthMeResponse me() {
        CustomUserDetails userDetails =
                (CustomUserDetails) SecurityContextHolder.getContext()
                        .getAuthentication()
                        .getPrincipal();

        User user = userDetails.getUser();

        return AuthMeResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .role(user.getRole())
                .build();
    }

//    public AuthMeResponse me() {
//        String email = SecurityContextHolder.getContext().getAuthentication().getName();
//
//        User user = userRepository.findByEmail(email)
//                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy người dùng"));
//        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
//
//        System.out.println("AUTH: " + auth);
//        System.out.println("PRINCIPAL CLASS: " + auth.getPrincipal().getClass());
//
//        return AuthMeResponse.builder()
//                .id(user.getId())
//                .email(user.getEmail())
//                .role(user.getRole())
//                .build();
//    }
}