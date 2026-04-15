package com.example.cdtn.controller;

import com.example.cdtn.dto.request.TeacherRequest;
import com.example.cdtn.dto.response.ApiResponse;
import com.example.cdtn.dto.response.TeacherResponse;
import com.example.cdtn.entity.enums.Status;
import com.example.cdtn.service.TeacherService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/admin/teachers")
public class TeacherController {
    private final TeacherService teacherService;
    public TeacherController(TeacherService teacherService){
        this.teacherService = teacherService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<TeacherResponse>>> getAllTeaachers(){
        List<TeacherResponse> teachers = teacherService.getAllTeachers();
        return ResponseEntity.ok(
                new ApiResponse<>(
                        "Lấy danh sách giảng viên thành công",
                        Status.SUCCESS,
                        "",
                        teachers
                )
        );
    }

    @PostMapping
    public ResponseEntity<ApiResponse<TeacherResponse>> createTeacher(@Valid @RequestBody TeacherRequest request){
        TeacherResponse teacher = teacherService.createTeacher(request);
        return ResponseEntity.ok(
                new ApiResponse<>(
                        "Tạo giảng viên thành công",
                        Status.SUCCESS,
                        "",
                        teacher
                )
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<TeacherResponse>> getTeacherByid(@PathVariable Long id){
        TeacherResponse teacher = teacherService.getTeacherById(id);
        return ResponseEntity.ok(
                new ApiResponse<>(
                        "Lấy thông tin giảng viên thành công",
                        Status.SUCCESS,
                        "",
                        teacher
                )
        );
    }
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<TeacherResponse>> updateTeacher(@PathVariable Long id,
                                                                         @Valid @RequestBody TeacherRequest request){
        TeacherResponse teacher = teacherService.updateTeacher(id, request);
        return ResponseEntity.ok(
                new ApiResponse<>(
                        "Cập nhật thông tin giảng viên thành công",
                        Status.SUCCESS,
                        "",
                        teacher
                )
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<?>> deleteTeacher(@PathVariable Long id){
        teacherService.deleteTeacher(id);
        return ResponseEntity.ok(
                new ApiResponse<>(
                        "Xoá giảng viên thành công",
                        Status.SUCCESS,
                        "",
                        null
                )
        );
    }
}
