package com.example.cdtn.controller;

import com.example.cdtn.dto.response.ApiResponse;
import com.example.cdtn.dto.response.MetaData;
import com.example.cdtn.dto.response.report.*;
import com.example.cdtn.entity.enums.Status;
import com.example.cdtn.service.ReportService;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/teachers/reports")
public class ReportController {

    private final ReportService reportService;
    public ReportController(ReportService reportService){
        this.reportService = reportService;
    }

    // 1. REPORT 1 BUỔI (SESSION)
    @GetMapping("/sessions/{sessionId}")
    public ResponseEntity<ApiResponse<List<SessionAttendanceResponse>>> getSessionReport(
            @PathVariable Long sessionId) {

        return ResponseEntity.ok(
                new ApiResponse<>(
                        "Lấy danh sách điểm danh theo buổi thành công",
                        Status.SUCCESS,
                        "",
                        reportService.getSessionReport(sessionId)
                )
        );
    }

    // 2. LỊCH SỬ 1 SINH VIÊN
    @GetMapping("/student/{id:\\d+}")
    public ResponseEntity<ApiResponse<List<StudentAttendanceResponse>>> getStudentHistory(
            @PathVariable Long id,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<StudentAttendanceResponse> result =
                reportService.getStudentHistory(id, page, size);
        return ResponseEntity.ok(
                new ApiResponse<>(
                        "Lấy lịch sử điểm danh sinh viên thành công",
                        Status.SUCCESS,
                        "",
                        result.getContent(),
                        new MetaData(
                                result.getNumber(),
                                result.getTotalPages()
                        )
                )
        );
    }

    // 3. TỔNG HỢP CLASS
    @GetMapping("/classes/{classId}")
    public ResponseEntity<ApiResponse<List<ClassMatrixResponse>>> getClassMatrix(
            @PathVariable Long classId) {

        return ResponseEntity.ok(
                new ApiResponse<>(
                        "Lấy báo cáo tổng hợp lớp thành công",
                        Status.SUCCESS,
                        "",
                        reportService.getClassMatrix(classId)
                )
        );
    }

    //EXPORT CLASSMATRIX
    @GetMapping("/class/{classId}/export")
    public ResponseEntity<ByteArrayResource> exportClassMatrix(@PathVariable Long classId) {

        ByteArrayResource file = reportService.exportClassMatrixExcel(classId);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=attendance.xlsx")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(file);
    }

    //EXPORT SESSION
    @GetMapping("/session/{sessionId}/export")
    public ResponseEntity<ByteArrayResource> exportSession(@PathVariable Long sessionId) {

        ByteArrayResource file = reportService.exportSessionExcel(sessionId);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=session.xlsx")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(file);
    }

    //LAY 5 DIEM DANH MOI NHAT
    @GetMapping("/recent/{classId:\\d+}")
    public ApiResponse<List<RecentAttendanceResponse>> getRecentByClass(@PathVariable Long classId) {

        return new ApiResponse<>(
                "Lấy 5 điểm danh mới nhất thành công",
                Status.SUCCESS,
                "",
                reportService.getRecentAttendancesByClass(classId)
        );
    }

    //THỐNG KÊ 1 TUẦN CỦA CÁC LỚP THUỘC TEACHER
    @GetMapping("/stats/weekly")
    public ApiResponse<WeeklyStatsResponse> getWeeklyStats() {

        return new ApiResponse<>(
                "Thống kê 1 tuần thành công",
                Status.SUCCESS,
                "",
                reportService.getWeeklyStats()
        );
    }

    //THỐNG KÊ 1 TUẦN CỦA 1 LỚP THUỘC TEACHER
    @GetMapping("/stats/class/{classId}")
    public ApiResponse<ClassStatsResponse> getClassStats(@PathVariable Long classId) {
        return new ApiResponse<>(
                "Thống kê lớp thành công",
                Status.SUCCESS,
                "",
                reportService.getWeeklyStatsByClass(classId)
        );
    }
}