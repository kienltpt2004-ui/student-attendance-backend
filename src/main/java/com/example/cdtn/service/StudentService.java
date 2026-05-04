package com.example.cdtn.service;

import com.example.cdtn.dto.request.StudentRequest;
import com.example.cdtn.dto.response.FaceVerifyResponse;
import com.example.cdtn.dto.response.StudentResponse;
import com.example.cdtn.entity.Student;
import com.example.cdtn.entity.User;
import com.example.cdtn.entity.enums.Role;
import com.example.cdtn.exception.BadRequestException;
import com.example.cdtn.exception.ResourceNotFoundException;
import com.example.cdtn.repository.StudentRepository;
import com.example.cdtn.repository.UserRepository;
import com.example.cdtn.security.CustomUserDetails;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

@Service
public class StudentService {
    private final StudentRepository studentRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final FaceApiService faceApiService;

    public StudentService(StudentRepository studentRepository,
                          UserRepository userRepository,
                          PasswordEncoder passwordEncoder,
                          FaceApiService faceApiService){
        this.studentRepository = studentRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.faceApiService = faceApiService;
    }

    private Student getCurrentStudent() {
        var auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !(auth.getPrincipal() instanceof CustomUserDetails user)) {
            throw new BadRequestException("Chưa đăng nhập");
        }

        return studentRepository.findByUser_Id(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy sinh viên"));
    }

    public StudentResponse createStudent(StudentRequest request){
        if(userRepository.existsByEmail(request.getEmail())){
            throw new BadRequestException("Email đã tồn tại");
        }
        if(studentRepository.existsByStudentCode(request.getStudentCode())){
            throw new BadRequestException("Mã sinh viên đã tồn tại");
        }
        if (request.getPhone() != null &&
                studentRepository.existsByPhone(request.getPhone())) {
            throw new BadRequestException("Số điện thoại đã tồn tại");
        }

        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(Role.STUDENT);

        User savedUser = userRepository.save(user);

        Student student= new Student();
        student.setStudentCode(request.getStudentCode());
        student.setFullName(request.getFullName());
        student.setPhone(request.getPhone());
        student.setGender(request.getGender());
        student.setDateOfBirth(request.getDateOfBirth());
        student.setAddress(request.getAddress());
        student.setDepartment(request.getDepartment());
        student.setUser(savedUser);

        studentRepository.save(student);
        return mapToResponse(student);

    }

    public Page<StudentResponse> getAllStudents(int page, int size){
        Pageable pageable = PageRequest.of(page, size);

        return studentRepository.findAll(pageable)
                .map(this::mapToResponse);
    }
    public StudentResponse getByStudentCode(String code){
        Student student = studentRepository.findByStudentCode(code)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy sinh viên"));

        return mapToResponse(student);
    }

    public StudentResponse getStudentById(Long id){
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Sinh viên không tồn tại với Id " + id));
        return mapToResponse(student);
    }
    @Transactional
    public StudentResponse updateStudent(Long id, StudentRequest request){
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Sinh viên không tồn tại với Id " + id));

        student.setFullName(request.getFullName());
        student.setPhone(request.getPhone());
        student.setGender(request.getGender());
        student.setDateOfBirth(request.getDateOfBirth());
        student.setAddress(request.getAddress());
        student.setDepartment(request.getDepartment());

        studentRepository.save(student);

        return mapToResponse(student);
    }

    public void deleteStudent(Long id){
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Sinh viên không tồn tại với Id " + id));

        studentRepository.delete(student);
    }
    @Transactional
    public void registerFace(String imageBase64) {

        if (imageBase64 == null || imageBase64.isEmpty()) {
            throw new BadRequestException("Ảnh không hợp lệ");
        }

        //lấy từ token
        Student student = getCurrentStudent();

        if (Boolean.TRUE.equals(student.getFaceRegistered())) {
            throw new BadRequestException("Sinh viên đã đăng kí khuôn mặt");
        }

        // 1. lấy embedding mới
        String newEmbedding = faceApiService.registerFace(imageBase64);

        // 2. check trùng
        List<Student> students = studentRepository.findByFaceRegisteredTrue();

        for (Student s : students) {

            if (s.getId().equals(student.getId())) continue;

            FaceVerifyResponse res = faceApiService.verifyFace(
                    imageBase64,
                    s.getFaceEmbedding()
            );

            double distance = res.getDistance();

            System.out.println("Compare with " + s.getId() + " -> " + distance);

            //threshold chuẩn
            if (distance < 0.4) {
                throw new BadRequestException("Khuôn mặt đã tồn tại trong hệ thống");
            }
        }

        // 3. save
        student.setFaceEmbedding(newEmbedding);
        student.setFaceRegistered(true);

        studentRepository.save(student);
    }
    @Transactional
    public void updateFace(String imageBase64) {

        if (imageBase64 == null || imageBase64.isEmpty()) {
            throw new BadRequestException("Ảnh không hợp lệ");
        }

        //lấy từ token
        Student student = getCurrentStudent();

        if (!Boolean.TRUE.equals(student.getFaceRegistered())) {
            throw new BadRequestException("Sinh viên chưa đăng ký khuôn mặt");
        }

        //lấy embedding mới
        String newEmbedding = faceApiService.registerFace(imageBase64);

        //check trùng
        List<Student> students =
                studentRepository.findByFaceRegisteredTrueAndIdNot(student.getId());

        for (Student s : students) {

            if (s.getFaceEmbedding() == null) continue;

            FaceVerifyResponse res = faceApiService.verifyFace(
                    imageBase64,
                    s.getFaceEmbedding()
            );

            double distance = res.getDistance();

            System.out.println("Compare with " + s.getId() + " -> " + distance);

            if (distance < 0.4) {
                throw new BadRequestException("Khuôn mặt đã tồn tại trong hệ thống");
            }
        }

        //save
        student.setFaceEmbedding(newEmbedding);

        studentRepository.save(student);
    }

    private double calculateDistance(List<Double> a, List<Double> b) {
        double sum = 0;

        for (int i = 0; i < a.size(); i++) {
            double diff = a.get(i) - b.get(i);
            sum += diff * diff;
        }

        return Math.sqrt(sum);
    }
    public List<Double> convert(String embedding) {
        return Arrays.stream(embedding.replace("[", "")
                        .replace("]", "")
                        .split(","))
                .map(Double::parseDouble)
                .toList();
    }

    private StudentResponse mapToResponse(Student student){
        return StudentResponse.builder()
                .id(student.getId())
                .studentCode(student.getStudentCode())
                .fullName(student.getFullName())
                .phone(student.getPhone())
                .gender(student.getGender())
                .dateOfBirth(student.getDateOfBirth())
                .address(student.getAddress())
                .department(student.getDepartment())
                .email(student.getUser().getEmail())
                .build();
    }
}
