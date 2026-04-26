package com.example.cdtn.controller;

import com.example.cdtn.dto.request.SessionRequest;
import com.example.cdtn.dto.response.ApiResponse;
import com.example.cdtn.dto.response.SessionResponse;
import com.example.cdtn.entity.Session;
import com.example.cdtn.entity.enums.Status;
import com.example.cdtn.service.SessionService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/teachers/sessions")
public class SessionController {
    private final SessionService service;
    public SessionController(SessionService service){
        this.service = service;
    }

    @GetMapping("/class/{classId}")
    public ResponseEntity<ApiResponse<List<SessionResponse>>> getAllSession(@PathVariable Long classId){
        return ResponseEntity.ok(
                new ApiResponse<>(
                        "Lấy danh sách session theo lớp thành công",
                        Status.SUCCESS,
                        "",
                        service.getAllSessionByClass(classId)
                )
        );
    }

    @GetMapping("{id:\\d+}")
    public ResponseEntity<ApiResponse<SessionResponse>> getSessionById(@PathVariable Long id){
        return ResponseEntity.ok(
                new ApiResponse<>(
                        "Lấy thông tin session thành công",
                        Status.SUCCESS,
                        "",
                        service.getSessionById(id)
                )
        );
    }

    @PostMapping
    public ResponseEntity<ApiResponse<SessionResponse>> createSession(@Valid @RequestBody SessionRequest request){
        SessionResponse session = service.createSession(request);

        return ResponseEntity.ok(
                new ApiResponse<>(
                        "Tạo session thành công",
                        Status.SUCCESS,
                        "",
                        session
                )
        );
    }

    @PostMapping("{id:\\d+}/close")
    public ResponseEntity<ApiResponse<?>> closeSession(@PathVariable Long id){
        service.closeSession(id);

        return ResponseEntity.ok(
                new ApiResponse<>(
                        "Đóng session thành công",
                        Status.SUCCESS,
                        "",
                        null
                )
        );
    }

}
