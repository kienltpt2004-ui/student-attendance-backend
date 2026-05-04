package com.example.cdtn.controller;


import com.example.cdtn.dto.request.ClassRequest;
import com.example.cdtn.dto.response.ApiResponse;
import com.example.cdtn.dto.response.ClassResponse;
import com.example.cdtn.dto.response.MetaData;
import com.example.cdtn.entity.enums.Status;
import com.example.cdtn.service.ClassesService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/classes")
@PreAuthorize("hasRole('ADMIN')") // chỉ admin truy cập
public class AdminClassesController {

    private final ClassesService classesService;

    public AdminClassesController(ClassesService classesService) {
        this.classesService = classesService;
    }

    //GET ALL
    @GetMapping
    public ResponseEntity<ApiResponse<?>> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Page<ClassResponse> classPage = classesService.getAllClasses(page, size);

        MetaData meta = new MetaData(
                classPage.getNumber(),
                classPage.getTotalPages()
        );

        return ResponseEntity.ok(
                new ApiResponse<>(
                        "Admin lấy danh sách tất cả lớp",
                        Status.SUCCESS,
                        "",
                        classPage.getContent(),
                        meta
                )
        );
    }

    //CREATE
    @PostMapping
    public ResponseEntity<ApiResponse<ClassResponse>> create(
            @Valid @RequestBody ClassRequest request
    ) {
        ClassResponse data = classesService.createForAdmin(request);

        return ResponseEntity.ok(
                new ApiResponse<>(
                        "Admin tạo lớp thành công",
                        Status.SUCCESS,
                        "",
                        data
                )
        );
    }

    //UPDATE
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ClassResponse>> update(
            @PathVariable Long id,
            @RequestBody ClassRequest request
    ) {
        ClassResponse data = classesService.updateForAdmin(id, request);

        return ResponseEntity.ok(
                new ApiResponse<>(
                        "Admin cập nhật lớp thành công",
                        Status.SUCCESS,
                        "",
                        data
                )
        );
    }

    //GET CLASS DETAIL (ADMIN)
    @GetMapping("/{id:\\d+}")
    public ResponseEntity<ApiResponse<ClassResponse>> getClassById(
            @PathVariable Long id,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size
    ) {

        ClassResponse data = classesService.getClassDetailForAdmin(id, page, size);

        MetaData metaData = new MetaData(
                page,
                (int) Math.ceil((double) data.getTotalStudents() / size)
        );

        return ResponseEntity.ok(
                new ApiResponse<>(
                        "Chi tiết lớp (admin)",
                        Status.SUCCESS,
                        "",
                        data,
                        metaData
                )
        );
    }

    //Add Student
    @PostMapping("/{classId}/students/{studentId}")
    public ResponseEntity<ApiResponse<?>> addStudent(
            @PathVariable Long classId,
            @PathVariable Long studentId
    ) {
        classesService.addStudentForAdmin(classId, studentId);

        return ResponseEntity.ok(
                new ApiResponse<>(
                        "Admin thêm sinh viên thành công",
                        Status.SUCCESS,
                        "",
                        null
                )
        );
    }

    //Remove Student
    @DeleteMapping("/{classId}/students/{studentId}")
    public ResponseEntity<ApiResponse<?>> removeStudent(
            @PathVariable Long classId,
            @PathVariable Long studentId
    ) {
        classesService.removeStudentForAdmin(classId, studentId);

        return ResponseEntity.ok(
                new ApiResponse<>(
                        "Admin xóa sinh viên thành công",
                        Status.SUCCESS,
                        "",
                        null
                )
        );
    }
}
