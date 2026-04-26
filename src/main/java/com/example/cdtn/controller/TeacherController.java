package com.example.cdtn.controller;

import com.example.cdtn.dto.request.TeacherRequest;
import com.example.cdtn.dto.response.ApiResponse;
import com.example.cdtn.dto.response.MetaData;
import com.example.cdtn.dto.response.StudentResponse;
import com.example.cdtn.dto.response.TeacherResponse;
import com.example.cdtn.entity.enums.Status;
import com.example.cdtn.service.TeacherService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
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
    public ResponseEntity<ApiResponse<List<TeacherResponse>>> getAllTeachers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ){
        Page<TeacherResponse> teacherPage = teacherService.getAllTeachers(page, size);

        MetaData meta = new MetaData(
                teacherPage.getNumber(),
                teacherPage.getTotalPages()
        );

        return ResponseEntity.ok(
                new ApiResponse<>(
                        "Lấy danh sách giảng viên thành công",
                        Status.SUCCESS,
                        "",
                        teacherPage.getContent(),
                        meta
                )
        );
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<TeacherResponse>> search(@RequestParam String teacherCode){
        TeacherResponse teacher = teacherService.getByTeacherCode(teacherCode);

        return ResponseEntity.ok(
                new ApiResponse<>(
                        "Lấy thông tin giảng viên thành công",
                        Status.SUCCESS,
                        "",
                        teacher
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
