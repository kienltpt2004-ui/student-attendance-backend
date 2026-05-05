package com.example.cdtn.controller;

import com.example.cdtn.dto.request.ClassRequest;
import com.example.cdtn.dto.response.ApiResponse;
import com.example.cdtn.dto.response.ClassResponse;
import com.example.cdtn.dto.response.MetaData;
import com.example.cdtn.dto.response.StudentResponse;
import com.example.cdtn.entity.enums.Status;
import com.example.cdtn.service.ClassesService;
import com.example.cdtn.service.StudentService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@RestController
@RequestMapping("api/teachers/classes")
public class ClassesController {

    private final ClassesService classesService;
    private final StudentService studentService;

    public ClassesController(ClassesService classesService, StudentService studentService) {
        this.classesService = classesService;
        this.studentService = studentService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<ClassResponse>>> getAllClass(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ){
        Page<ClassResponse> classPage = classesService.getMyClasses(page, size);

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
                        classesService.createForTeacher(request)
                )
        );
    }

    @GetMapping("/{id:\\d+}")
    public ResponseEntity<ApiResponse<ClassResponse>> getByClassId(
            @PathVariable Long id,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size
    ) {

        ClassResponse data = classesService.getClassDetailForTeacher(id, page, size);

        MetaData metaData = new MetaData(
                page,
                (int) Math.ceil((double) data.getTotalStudents() / size)
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
                        classesService.updateForTeacher(id, request)
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
    @GetMapping("/students")
    public ResponseEntity<ApiResponse<List<StudentResponse>>> getAllStudents(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ){
        Page<StudentResponse> studentPage = studentService.getAllStudents(page, size);

        return ResponseEntity.ok(
                new ApiResponse<>(
                        "Lấy danh sách sinh viên thành công",
                        Status.SUCCESS,
                        "",
                        studentPage.getContent(),
                        new MetaData(
                                studentPage.getNumber(),
                                studentPage.getTotalPages()
                        )
                )
        );
    }
    @GetMapping("/students/search")
    public ResponseEntity<ApiResponse<StudentResponse>> search(@RequestParam String studentCode){
        StudentResponse student = studentService.getByStudentCode(studentCode);

        return ResponseEntity.ok(
                new ApiResponse<>(
                        "Lấy sinh viên thành công",
                        Status.SUCCESS,
                        "",
                        student
                )
        );
    }
}
