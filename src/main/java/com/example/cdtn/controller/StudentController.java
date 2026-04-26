package com.example.cdtn.controller;

import com.example.cdtn.dto.request.FaceRequest;
import com.example.cdtn.dto.request.StudentRequest;
import com.example.cdtn.dto.response.ApiResponse;
import com.example.cdtn.dto.response.MetaData;
import com.example.cdtn.dto.response.StudentResponse;
import com.example.cdtn.dto.response.report.StudentAttendanceResponse;
import com.example.cdtn.entity.enums.Status;
import com.example.cdtn.exception.BadRequestException;
import com.example.cdtn.service.ReportService;
import com.example.cdtn.service.StudentService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api")
public class StudentController {
    private final StudentService studentService;
    private final ReportService reportService;
    public StudentController(StudentService studentService,
                             ReportService reportService){
        this.studentService = studentService;
        this.reportService = reportService;
    }

    @GetMapping("/admin/students")
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

    @GetMapping("/admin/students/search-bycode")
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

    @GetMapping("/admin/students/{id:\\d+}")
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

    @PostMapping("/admin/students")
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

    @PutMapping("/admin/students/{id}")
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

    @DeleteMapping("/admin/students/{id}")
    public ApiResponse<?> deleteStudent(@PathVariable Long id){
        studentService.deleteStudent(id);
        return new ApiResponse<>(
                "Xoá sinh viên thành công",
                Status.SUCCESS,
                "",
                null
        );
    }

    @PostMapping("/students/me/face")
    public ResponseEntity<ApiResponse<?>> registerFace(
            @RequestBody FaceRequest request
    ) {
        studentService.registerFace(request.getImage());
        return ResponseEntity.ok(
                new ApiResponse<>(
                        "Đăng ký khuôn mặt thành công",
                        Status.SUCCESS,
                        "",
                        null
                )
        );
    }

    @PutMapping("/students/me/face-update")
    public ResponseEntity<ApiResponse<?>> updateFace(
            @RequestBody FaceRequest request
    ) {
        studentService.updateFace( request.getImage());
        return ResponseEntity.ok(
                new ApiResponse<>(
                        "Cập nhật khuôn mặt thành công",
                        Status.SUCCESS,
                        "",
                        null
                )
        );
    }

    @GetMapping("/students/me/attendance")
    public ResponseEntity<ApiResponse<List<StudentAttendanceResponse>>> getMyAttendance(
            Authentication authentication,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new BadRequestException("Chưa đăng nhập");
        }

        String email = authentication.getName();
        Page<StudentAttendanceResponse> result =
                reportService.getMyAttendance(email, page, size);
        return ResponseEntity.ok(
                new ApiResponse<>(
                        "Lấy lịch sử điểm danh thành công",
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
}
