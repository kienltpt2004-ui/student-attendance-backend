package com.example.cdtn.controller;

import com.example.cdtn.dto.request.ClassRequest;
import com.example.cdtn.dto.response.ApiResponse;
import com.example.cdtn.dto.response.ClassResponse;
import com.example.cdtn.dto.response.MetaData;
import com.example.cdtn.dto.response.StudentResponse;
import com.example.cdtn.entity.ClassStudent;
import com.example.cdtn.entity.Classes;
import com.example.cdtn.entity.enums.Status;
import com.example.cdtn.service.ClassesService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@RestController
@RequestMapping("api/teachers/classes")
public class ClassesController {

    private final ClassesService classesService;

    public ClassesController(ClassesService classesService) {
        this.classesService = classesService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<ClassResponse>>> getAllClass(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ){
        Page<ClassResponse> classPage = classesService.getAllClass(page, size);

        MetaData meta = new MetaData(
                classPage.getNumber(),
                classPage.getTotalPages()
        );

        return ResponseEntity.ok(
                new ApiResponse<>(
                        "Lấy danh sách lớp thành công",
                        Status.SUCCESS,
                        "",
                        classPage.getContent(),
                        meta
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

    @GetMapping("/{id:\\d+}")
    public ResponseEntity<ApiResponse<ClassResponse>> getByClassId(@PathVariable Long id,
                                                                   @RequestParam(defaultValue = "0") int page,
                                                                   @RequestParam(defaultValue = "5") int size) {
        ClassResponse data = classesService.getClassById(id, page, size);
        Page<StudentResponse> pageData = classesService.getStudentPage(id, page, size);
        MetaData metaData = new MetaData(
                page,
                pageData.getTotalPages()
        );
        return ResponseEntity.ok(
                new ApiResponse<>(
                        "Chi tiết lớp",
                        Status.SUCCESS,
                        "",
                        data,
                        metaData
                )
        );
    }
    @PutMapping("/{id:\\d+}")
    public ResponseEntity<ApiResponse<ClassResponse>> updateClass(@PathVariable Long id,
                                                                  @Valid @RequestBody ClassRequest request){
        return ResponseEntity.ok(
                new ApiResponse<>(
                        "Cập nhật lớp thành công",
                        Status.SUCCESS,
                        "",
                        classesService.updateClass(id, request)
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
