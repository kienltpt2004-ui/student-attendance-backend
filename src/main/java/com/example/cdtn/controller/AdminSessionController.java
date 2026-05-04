package com.example.cdtn.controller;

import com.example.cdtn.dto.request.SessionRequest;
import com.example.cdtn.dto.response.ApiResponse;
import com.example.cdtn.dto.response.SessionResponse;
import com.example.cdtn.entity.enums.Status;
import com.example.cdtn.service.SessionService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/sessions")
@PreAuthorize("hasRole('ADMIN')")
public class AdminSessionController {

    private final SessionService sessionService;

    public AdminSessionController(SessionService sessionService) {
        this.sessionService = sessionService;
    }

    //GET ALL SESSION BY CLASS
    @GetMapping("/class/{classId}")
    public ResponseEntity<ApiResponse<List<SessionResponse>>> getAllByClass(
            @PathVariable Long classId
    ) {
        List<SessionResponse> data = sessionService.getAllSessions(classId);

        return ResponseEntity.ok(
                new ApiResponse<>(
                        "Admin lấy danh sách session theo lớp",
                        Status.SUCCESS,
                        "",
                        data
                )
        );
    }

    //GET SESSION BY ID
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<SessionResponse>> getById(
            @PathVariable Long id
    ) {
        SessionResponse data = sessionService.getByIdForAdmin(id);

        return ResponseEntity.ok(
                new ApiResponse<>(
                        "Admin xem chi tiết session",
                        Status.SUCCESS,
                        "",
                        data
                )
        );
    }
    @PostMapping
    public ResponseEntity<ApiResponse<SessionResponse>> create(
            @Valid @RequestBody SessionRequest request
    ) {

        SessionResponse data = sessionService.createForAdmin(request);

        return ResponseEntity.ok(
                new ApiResponse<>(
                        "Admin tạo session thành công",
                        Status.SUCCESS,
                        "",
                        data
                )
        );
    }

    //CLOSE SESSION
    @PostMapping("/{id}/close")
    public ResponseEntity<ApiResponse<?>> closeSession(
            @PathVariable Long id
    ) {
        sessionService.closeForAdmin(id);

        return ResponseEntity.ok(
                new ApiResponse<>(
                        "Admin đóng session thành công",
                        Status.SUCCESS,
                        "",
                        null
                )
        );
    }
}
