package com.example.cdtn.controller;

import com.example.cdtn.dto.request.StudentRequest;
import com.example.cdtn.dto.response.ApiResponse;
import com.example.cdtn.dto.response.StudentResponse;
import com.example.cdtn.entity.Student;
import com.example.cdtn.entity.enums.Status;
import com.example.cdtn.service.StudentService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/admin/students")
public class StudentController {
    private final StudentService studentService;
    public StudentController(StudentService studentService){
        this.studentService = studentService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<StudentResponse>>> getAllStudents(){
        List<StudentResponse> students = studentService.getAllStudents();
        return ResponseEntity.ok(
                new ApiResponse<>(
                        "Lấy danh sách sinh viên thành công",
                        Status.SUCCESS,
                        "",
                        students
                )
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<StudentResponse>> getStudentById(@PathVariable Long id){
        StudentResponse student = studentService.getStudentById(id);
        return ResponseEntity.ok(
                new ApiResponse<>(
                        "Lấy sinh viên thành công",
                        Status.SUCCESS,
                        "",
                        student
                )
        );
    }

    @PostMapping
    public ResponseEntity<ApiResponse<StudentResponse>> createStudent(@Valid @RequestBody StudentRequest request){
        StudentResponse student = studentService.createStudent(request);
        return ResponseEntity.ok(
                new ApiResponse<>(
                        "Thêm sinh viên thành công",
                        Status.SUCCESS,
                        "",
                        student
                )
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<StudentResponse>> updateStudent(@PathVariable Long id,
                                                                      @Valid @RequestBody StudentRequest request){
        StudentResponse student = studentService.updateStudent(id,request);
        return ResponseEntity.ok(
                new ApiResponse<>(
                        "Cập nhật thông tin sinh viên thành công",
                        Status.SUCCESS,
                        "",
                        student
                )
        );
    }

    @DeleteMapping("/{id}")
    public ApiResponse<?> deleteStudent(@PathVariable Long id){
        studentService.deleteStudent(id);
        return new ApiResponse<>(
                "Xoá sinh viên thành công",
                Status.SUCCESS,
                "",
                null
        );
    }

}
