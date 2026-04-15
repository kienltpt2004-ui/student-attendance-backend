package com.example.cdtn.controller;

import com.example.cdtn.dto.request.ClassRequest;
import com.example.cdtn.dto.response.ApiResponse;
import com.example.cdtn.dto.response.ClassResponse;
import com.example.cdtn.entity.Classes;
import com.example.cdtn.entity.enums.Status;
import com.example.cdtn.service.ClassesService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@RestController
@RequestMapping("api/teacher/classes")
public class ClassesController {

    private final ClassesService classesService;

    public ClassesController(ClassesService classesService) {
        this.classesService = classesService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<ClassResponse>>> getAll() {
        return ResponseEntity.ok(
                new ApiResponse<>(
                        "Danh sách lớp",
                        Status.SUCCESS,
                        "",
                        classesService.getAllClasses()
                )
        );
    }

    @PostMapping
    public ResponseEntity<ApiResponse<ClassResponse>> createClass(@Valid @RequestBody ClassRequest request) {
        return ResponseEntity.ok(
                new ApiResponse<>(
                        "Tạo lớp thành công",
                        Status.SUCCESS,
                        "",
                        classesService.createClass(request)
                )
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ClassResponse>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(
                new ApiResponse<>(
                        "Chi tiết lớp",
                        Status.SUCCESS,
                        "",
                        classesService.getClassById(id)
                )
        );
    }

    @PostMapping("/{classId}/students/{studentId}")
    public ResponseEntity<ApiResponse<?>> addStudent(@PathVariable Long classId,
                                                     @PathVariable Long studentId) {

        classesService.addStudent(classId, studentId);

        return ResponseEntity.ok(
                new ApiResponse<>(
                        "Thêm sinh viên thành công",
                        Status.SUCCESS,
                        "",
                        null)
        );
    }

    @DeleteMapping("/{classId}/students/{studentId}")
    public ResponseEntity<ApiResponse<?>> removeStudent(@PathVariable Long classId,
                                                        @PathVariable Long studentId) {

        classesService.removeStudent(classId, studentId);

        return ResponseEntity.ok(
                new ApiResponse<>(
                        "Xóa sinh viên thành công",
                        Status.SUCCESS,
                        "",
                        null)
        );
    }
}
