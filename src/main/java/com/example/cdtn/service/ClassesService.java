package com.example.cdtn.service;

import com.example.cdtn.dto.request.ClassRequest;
import com.example.cdtn.dto.response.ClassResponse;
import com.example.cdtn.dto.response.StudentResponse;
import com.example.cdtn.entity.ClassStudent;
import com.example.cdtn.entity.Classes;
import com.example.cdtn.entity.Student;
import com.example.cdtn.entity.Teacher;
import com.example.cdtn.exception.BadRequestException;
import com.example.cdtn.exception.ResourceNotFoundException;
import com.example.cdtn.repository.ClassStudentRepository;
import com.example.cdtn.repository.ClassesRepository;
import com.example.cdtn.repository.StudentRepository;
import com.example.cdtn.repository.TeacherRepository;
import com.example.cdtn.security.CustomUserDetails;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ClassesService {

    private final ClassesRepository classesRepository;
    private final TeacherRepository teacherRepository;
    private final StudentRepository studentRepository;
    private final ClassStudentRepository classStudentRepository;

    public ClassesService(ClassesRepository classesRepository,
                          TeacherRepository teacherRepository,
                          StudentRepository studentRepository,
                          ClassStudentRepository classStudentRepository) {
        this.classesRepository = classesRepository;
        this.teacherRepository = teacherRepository;
        this.studentRepository = studentRepository;
        this.classStudentRepository = classStudentRepository;
    }

    private CustomUserDetails getCurrentUser() {
        var auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !(auth.getPrincipal() instanceof CustomUserDetails user)) {
            throw new BadRequestException("Chưa đăng nhập");
        }

        return user;
    }


    //GET CURRENT TEACHER
    private Teacher getCurrentTeacher() {
        CustomUserDetails user = getCurrentUser();

        return teacherRepository.findByUser_Id(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy teacher"));
    }

    //CREATE CLASS
    public ClassResponse createClass(ClassRequest request) {

        Teacher teacher = getCurrentTeacher(); // FIX

        Classes classes = new Classes();
        classes.setClassroom(request.getClassroom());
        classes.setName(request.getName());
        classes.setSubjectName(request.getSubjectName());
        classes.setTeacher(teacher);

        classesRepository.save(classes);

        return mapToResponse(classes);
    }

    //GET ALL CLASS
    public Page<ClassResponse> getAllClass(int page, int size) {

        Teacher teacher = getCurrentTeacher();

        Pageable pageable = PageRequest.of(page, size);

        return classesRepository.findByTeacher_Id(teacher.getId(), pageable)
                .map(this::mapToResponse);
    }

    //GET CLASS DETAIL
    public ClassResponse getClassById(Long id, int page, int size) {

        Teacher teacher = getCurrentTeacher();

        Classes classes = classesRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy lớp"));

        if (!classes.getTeacher().getId().equals(teacher.getId())) {
            throw new BadRequestException("Bạn không có quyền xem lớp này");
        }

        Pageable pageable = PageRequest.of(page, size);

        Page<ClassStudent> studentPage =
                classStudentRepository.findPageByClasses_Id(id, pageable);

        return mapToDetailResponse(classes, studentPage);
    }

    //UPDATE CLASS
    public ClassResponse updateClass(Long id, ClassRequest request) {

        Teacher teacher = getCurrentTeacher();

        Classes classes = classesRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Lớp không tồn tại"));

        if (!classes.getTeacher().getId().equals(teacher.getId())) {
            throw new BadRequestException("Không có quyền sửa lớp này");
        }

        classes.setClassroom(request.getClassroom());
        classes.setName(request.getName());
        classes.setSubjectName(request.getSubjectName());

        classesRepository.save(classes);

        return mapToResponse(classes);
    }

    //ADD STUDENT
    @Transactional
    public void addStudent(Long classId, Long studentId) {

        Teacher teacher = getCurrentTeacher(); // FIX

        Classes classes = classesRepository.findById(classId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy lớp"));

        if (!classes.getTeacher().getId().equals(teacher.getId())) {
            throw new BadRequestException("Không có quyền");
        }

        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy sinh viên"));

        if (classStudentRepository.existsByClassesAndStudent(classes, student)) {
            throw new BadRequestException("Sinh viên đã tồn tại");
        }

        ClassStudent cs = new ClassStudent();
        cs.setClasses(classes);
        cs.setStudent(student);

        classStudentRepository.save(cs);
    }

    //REMOVE STUDENT
    @Transactional
    public void removeStudent(Long classId, Long studentId) {

        Teacher teacher = getCurrentTeacher(); // FIX

        Classes classes = classesRepository.findById(classId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy lớp"));

        if (!classes.getTeacher().getId().equals(teacher.getId())) {
            throw new BadRequestException("Không có quyền");
        }

        ClassStudent cs = classStudentRepository
                .findByClasses_IdAndStudent_Id(classId, studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tồn tại"));

        classStudentRepository.delete(cs);
    }

    //GET STUDENT PAGE
    public Page<StudentResponse> getStudentPage(Long classId, int page, int size) {

        Teacher teacher = getCurrentTeacher(); // FIX

        Classes classes = classesRepository.findById(classId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy lớp"));

        if (!classes.getTeacher().getId().equals(teacher.getId())) {
            throw new BadRequestException("Bạn không có quyền xem danh sách sinh viên");
        }

        Pageable pageable = PageRequest.of(page, size);

        return classStudentRepository.findPageByClasses_Id(classId, pageable)
                .map(cs -> {
                    Student student = cs.getStudent();
                    return StudentResponse.builder()
                            .id(student.getId())
                            .studentCode(student.getStudentCode())
                            .fullName(student.getFullName())
                            .build();
                });
    }

    //MAPPING
    private ClassResponse mapToResponse(Classes classes) {

        int total = classStudentRepository.countByClasses_Id(classes.getId());

        return ClassResponse.builder()
                .id(classes.getId())
                .classroom(classes.getClassroom())
                .name(classes.getName())
                .subjectName(classes.getSubjectName())
                .teacherId(classes.getTeacher().getId())
                .teacherName(classes.getTeacher().getFullName())
                .totalStudents(total)
                .build();
    }

    private ClassResponse mapToDetailResponse(Classes classes, Page<ClassStudent> studentPage) {

        int total = (int) studentPage.getTotalElements();

        List<StudentResponse> students = studentPage.getContent()
                .stream()
                .map(cs -> {
                    Student student = cs.getStudent();
                    return StudentResponse.builder()
                            .id(student.getId())
                            .studentCode(student.getStudentCode())
                            .fullName(student.getFullName())
                            .build();
                })
                .toList();

        return ClassResponse.builder()
                .id(classes.getId())
                .classroom(classes.getClassroom())
                .name(classes.getName())
                .subjectName(classes.getSubjectName())
                .teacherId(classes.getTeacher().getId())
                .teacherName(classes.getTeacher().getFullName())
                .totalStudents(total)
                .students(students)
                .build();
    }
}