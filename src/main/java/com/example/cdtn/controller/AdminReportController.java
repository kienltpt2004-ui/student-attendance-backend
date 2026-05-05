package com.example.cdtn.controller;

import com.example.cdtn.dto.response.ApiResponse;
import com.example.cdtn.dto.response.MetaData;
import com.example.cdtn.dto.response.report.*;
import com.example.cdtn.entity.enums.Status;
import com.example.cdtn.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/reports")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class AdminReportController {

    private final ReportService reportService;

    // 1. DASHBOARD TOÀN HỆ THỐNG
    @GetMapping("/system-stats")
    public ResponseEntity<ApiResponse<WeeklyStatsResponse>> getSystemStats() {

        WeeklyStatsResponse data =
                reportService.getSystemStatsForAdmin();

        return ResponseEntity.ok(
                new ApiResponse<>(
                        "Thống kê toàn hệ thống",
                        Status.SUCCESS,
                        "",
                        data
                )
        );
    }

    // 2. THỐNG KÊ 1 LỚP
    @GetMapping("/class/{classId}/stats")
    public ResponseEntity<ApiResponse<ClassStatsResponse>> getClassStats(
            @PathVariable Long classId
    ) {

        ClassStatsResponse data =
                reportService.getClassStatsForAdmin(classId);

        return ResponseEntity.ok(
                new ApiResponse<>(
                        "Thống kê lớp",
                        Status.SUCCESS,
                        "",
                        data
                )
        );
    }

    // 3. REPORT SESSION
    @GetMapping("/session/{sessionId}")
    public ResponseEntity<ApiResponse<List<SessionAttendanceResponse>>>
    getSessionReport(
            @PathVariable Long sessionId
    ) {

        List<SessionAttendanceResponse> data =
                reportService.getSessionReportForAdmin(sessionId);

        return ResponseEntity.ok(
                new ApiResponse<>(
                        "Report session",
                        Status.SUCCESS,
                        "",
                        data
                )
        );
    }

    // 4. MATRIX ĐIỂM DANH LỚP
    @GetMapping("/class/{classId}/matrix")
    public ResponseEntity<ApiResponse<List<ClassMatrixResponse>>>
    getClassMatrix(
            @PathVariable Long classId
    ) {

        List<ClassMatrixResponse> data =
                reportService.getClassMatrixForAdmin(classId);

        return ResponseEntity.ok(
                new ApiResponse<>(
                        "Ma trận điểm danh lớp",
                        Status.SUCCESS,
                        "",
                        data
                )
        );
    }

    // 5. LỊCH SỬ ĐIỂM DANH SINH VIÊN
    @GetMapping("/student/{studentId}")
    public ResponseEntity<ApiResponse<List<StudentAttendanceResponse>>>
    getStudentHistory(
            @PathVariable Long studentId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {

        Page<StudentAttendanceResponse> pageData =
                reportService.getStudentHistoryForAdmin(
                        studentId,
                        page,
                        size
                );

        MetaData metaData = new MetaData(
                pageData.getNumber(),
                pageData.getTotalPages()
        );

        return ResponseEntity.ok(
                new ApiResponse<>(
                        "Lịch sử điểm danh sinh viên",
                        Status.SUCCESS,
                        "",
                        pageData.getContent(),
                        metaData
                )
        );
    }

    // 6. EXPORT MATRIX EXCEL
    @GetMapping("/class/{classId}/export")
    public ResponseEntity<ByteArrayResource> exportClassMatrix(
            @PathVariable Long classId
    ) {

        ByteArrayResource file =
                reportService.exportClassMatrixExcelForAdmin(classId);

        return ResponseEntity.ok()
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=class-matrix.xlsx"
                )
                .contentType(
                        MediaType.parseMediaType(
                                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
                        )
                )
                .body(file);
    }

    // 7. EXPORT SESSION EXCEL
    @GetMapping("/session/{sessionId}/export")
    public ResponseEntity<ByteArrayResource> exportSession(
            @PathVariable Long sessionId
    ) {

        ByteArrayResource file =
                reportService.exportSessionExcelForAdmin(sessionId);

        return ResponseEntity.ok()
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=session-report.xlsx"
                )
                .contentType(
                        MediaType.parseMediaType(
                                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
                        )
                )
                .body(file);
    }
}