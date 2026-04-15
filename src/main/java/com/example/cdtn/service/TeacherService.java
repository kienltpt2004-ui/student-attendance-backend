package com.example.cdtn.service;

import com.example.cdtn.dto.request.StudentRequest;
import com.example.cdtn.dto.request.TeacherRequest;
import com.example.cdtn.dto.response.StudentResponse;
import com.example.cdtn.dto.response.TeacherResponse;
import com.example.cdtn.entity.Student;
import com.example.cdtn.entity.Teacher;
import com.example.cdtn.entity.User;
import com.example.cdtn.entity.enums.Role;
import com.example.cdtn.exception.BadRequestException;
import com.example.cdtn.exception.ResourceNotFoundException;
import com.example.cdtn.repository.TeacherRepository;
import com.example.cdtn.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TeacherService {
    private final UserRepository userRepository;
    private final TeacherRepository teacherRepository;
    private final PasswordEncoder passwordEncoder;


    public TeacherService(UserRepository userRepository,
                          TeacherRepository teacherRepository,
                          PasswordEncoder passwordEncoder){
        this.userRepository = userRepository;
        this.teacherRepository = teacherRepository;
        this.passwordEncoder = passwordEncoder;

    }

    public TeacherResponse createTeacher(TeacherRequest request){
        if(userRepository.existsByEmail(request.getEmail())){
            throw new BadRequestException("Email đã tồn tại");
        }
        if(teacherRepository.existsByTeacherCode(request.getTeacherCode())){
            throw new BadRequestException("Mã giảng viên đã tồn tại");
        }

        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(Role.TEACHER);

        User savedUser = userRepository.save(user);

        Teacher teacher = new Teacher();
        teacher.setTeacherCode(request.getTeacherCode());
        teacher.setFullName(request.getFullName());
        teacher.setAge(request.getAge());
        teacher.setUser(savedUser);

        teacherRepository.save(teacher);
        return mapToResponse(teacher);
    }

    public List<TeacherResponse> getAllTeachers(){

        return teacherRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    public TeacherResponse getTeacherById(Long id){
        Teacher teacher = teacherRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Giảng viên không tồn tại với Id " + id));
        return mapToResponse(teacher);
    }

    public TeacherResponse updateTeacher(Long id, TeacherRequest request){
        Teacher teacher = teacherRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Giảng viên không tồn tại với Id " + id));

        teacher.setFullName(request.getFullName());
        teacher.setAge(request.getAge());

        teacherRepository.save(teacher);

        return mapToResponse(teacher);
    }

    public void deleteTeacher(Long id){
        Teacher teacher = teacherRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Giảng viên không tồn tại với Id " + id));

        teacherRepository.delete(teacher);
    }

    private TeacherResponse mapToResponse(Teacher teacher){
        return TeacherResponse.builder()
                .id(teacher.getId())
                .teacherCode(teacher.getTeacherCode())
                .fullName(teacher.getFullName())
                .age(teacher.getAge())
                .email(teacher.getUser().getEmail())
                .build();
    }
}
