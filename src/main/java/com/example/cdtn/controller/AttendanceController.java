package com.example.cdtn.controller;

import com.example.cdtn.dto.request.AttendanceRequest;
import com.example.cdtn.dto.response.ApiResponse;
import com.example.cdtn.dto.response.AttendanceResponse;
import com.example.cdtn.entity.enums.Status;
import com.example.cdtn.service.AttendanceService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/students/attendance")
public class AttendanceController {
    private final AttendanceService attendanceService;

    public AttendanceController(AttendanceService attendanceService){
        this.attendanceService = attendanceService;
    }

    @PostMapping("/face")
    public ResponseEntity<ApiResponse<AttendanceResponse>> checkIn(@RequestBody @Valid AttendanceRequest request){
        AttendanceResponse response = attendanceService.checkIn(request);

        return ResponseEntity.ok(
                new ApiResponse<>(
                        "Điểm danh thành công",
                        Status.SUCCESS,
                        "",
                        response
                )
        );
    }
}
