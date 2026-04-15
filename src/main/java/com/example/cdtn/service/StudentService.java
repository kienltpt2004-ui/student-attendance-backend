package com.example.cdtn.service;

import com.example.cdtn.dto.request.StudentRequest;
import com.example.cdtn.dto.response.StudentResponse;
import com.example.cdtn.entity.Student;
import com.example.cdtn.entity.User;
import com.example.cdtn.entity.enums.Role;
import com.example.cdtn.exception.BadRequestException;
import com.example.cdtn.exception.ResourceNotFoundException;
import com.example.cdtn.repository.StudentRepository;
import com.example.cdtn.repository.UserRepository;
import lombok.Builder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
//@Builder
public class StudentService {
    private final StudentRepository studentRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public StudentService(StudentRepository studentRepository,
                          UserRepository userRepository,
                          PasswordEncoder passwordEncoder){
        this.studentRepository = studentRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public StudentResponse createStudent(StudentRequest request){
        if(userRepository.existsByEmail(request.getEmail())){
            throw new BadRequestException("Email đã tồn tại");
        }
        if(studentRepository.existsByStudentCode(request.getStudentCode())){
            throw new BadRequestException("Mã sinh viên đã tồn tại");
        }

        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(Role.STUDENT);

        User savedUser = userRepository.save(user);

        Student student= new Student();
        student.setStudentCode(request.getStudentCode());
        student.setFullName(request.getFullName());
        student.setAge(request.getAge());
        student.setDepartment(request.getDepartment());
        student.setUser(savedUser);

        studentRepository.save(student);
        return mapToResponse(student);

    }

    public List<StudentResponse> getAllStudents() {

        return studentRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    public StudentResponse getStudentById(Long id){
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Sinh viên không tồn tại với Id " + id));
        return mapToResponse(student);
    }

    public StudentResponse updateStudent(Long id, StudentRequest request){
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Sinh viên không tồn tại với Id " + id));

        student.setFullName(request.getFullName());
        student.setAge(request.getAge());
        student.setDepartment(request.getDepartment());

        studentRepository.save(student);

        return mapToResponse(student);
    }

    public void deleteStudent(Long id){
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Sinh viên không tồn tại với Id " + id));

        studentRepository.delete(student);
    }

    public void registerFace(Long studentId, String imageBase64){
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Sinh viên không tồn tại với Id " + studentId));

        if(student.getFaceRegistered()){
            throw new BadRequestException("Sinh viên đã đăng kí khuôn mặt");
        }


    }

    private StudentResponse mapToResponse(Student student){
        return StudentResponse.builder()
                .id(student.getId())
                .studentCode(student.getStudentCode())
                .fullName(student.getFullName())
                .age(student.getAge())
                .department(student.getDepartment())
                .email(student.getUser().getEmail())
                .build();
    }
}
