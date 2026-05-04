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

    private Teacher getCurrentTeacher() {
        return teacherRepository.findByUser_Id(getCurrentUser().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy teacher"));
    }

    private Classes getClassForTeacher(Long classId) {
        Teacher teacher = getCurrentTeacher();

        return classesRepository
                .findByIdAndTeacher_Id(classId, teacher.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Không có quyền hoặc không tìm thấy lớp"));
    }

    private Classes getClassOrThrow(Long classId) {
        return classesRepository.findById(classId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy lớp"));
    }

    // Teacher
    public Page<ClassResponse> getMyClasses(int page, int size) {
        Long teacherId = getCurrentTeacher().getId();

        return classesRepository.findByTeacher_Id(teacherId, PageRequest.of(page, size))
                .map(this::mapToResponse);
    }

    // Admin
    public Page<ClassResponse> getAllClasses(int page, int size) {
        return classesRepository.findAll(PageRequest.of(page, size))
                .map(this::mapToResponse);
    }

    //CREATE
    // Teacher
    public ClassResponse createForTeacher(ClassRequest request) {
        return create(request, getCurrentTeacher());
    }

    // Admin
    public ClassResponse createForAdmin(ClassRequest request) {
        Teacher teacher = teacherRepository.findById(request.getTeacherId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy teacher"));

        return create(request, teacher);
    }

    private ClassResponse create(ClassRequest request, Teacher teacher) {
        Classes classes = new Classes();
        classes.setName(request.getName());
        classes.setClassroom(request.getClassroom());
        classes.setSubjectName(request.getSubjectName());
        classes.setTeacher(teacher);

        classesRepository.save(classes);
        return mapToResponse(classes);
    }

    //STUDENT
    // Teacher only
    public void addStudent(Long classId, Long studentId) {
        Classes classes = getClassForTeacher(classId);

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

    //ADMIN - ADD STUDENT
    public void addStudentForAdmin(Long classId, Long studentId) {

        //ADMIN: không check teacher
        Classes classes = getClassOrThrow(classId);

        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy sinh viên"));
        if (classStudentRepository.existsByClassesAndStudent(classes, student)) {
            throw new BadRequestException("Sinh viên đã tồn tại trong lớp");
        }

        ClassStudent cs = new ClassStudent();
        cs.setClasses(classes);
        cs.setStudent(student);

        classStudentRepository.save(cs);
    }
    //Teacher
    public void removeStudent(Long classId, Long studentId) {
        Classes classes = getClassForTeacher(classId);

        ClassStudent cs = classStudentRepository
                .findByClasses_IdAndStudent_Id(classId, studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tồn tại"));

        classStudentRepository.delete(cs);
    }

    //ADMIN - REMOVE STUDENT
    public void removeStudentForAdmin(Long classId, Long studentId) {

        Classes classes = getClassOrThrow(classId);

        ClassStudent cs = classStudentRepository
                .findByClasses_IdAndStudent_Id(classId, studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Sinh viên không tồn tại trong lớp"));

        classStudentRepository.delete(cs);
    }
    public ClassResponse getClassDetailForTeacher(Long id, int page, int size) {

        Teacher teacher = getCurrentTeacher();

        Classes classes = classesRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy lớp"));

        //CHECK QUYỀN
        if (!classes.getTeacher().getId().equals(teacher.getId())) {
            throw new BadRequestException("Bạn không có quyền xem lớp này");
        }

        Pageable pageable = PageRequest.of(page, size);

        Page<ClassStudent> studentPage =
                classStudentRepository.findPageByClasses_Id(id, pageable);

        List<StudentResponse> students = studentPage.getContent()
                .stream()
                .map(cs -> mapStudent(cs.getStudent()))
                .toList();

        return ClassResponse.builder()
                .id(classes.getId())
                .classroom(classes.getClassroom())
                .name(classes.getName())
                .subjectName(classes.getSubjectName())
                .teacherId(classes.getTeacher().getId())
                .teacherName(classes.getTeacher().getFullName())
                .totalStudents((int) studentPage.getTotalElements())
                .students(students)
                .build();
    }
    //CLASS DETAIL (ADMIN)
    public ClassResponse getClassDetailForAdmin(Long id, int page, int size) {

        Classes classes = getClassOrThrow(id);

        Pageable pageable = PageRequest.of(page, size);

        Page<ClassStudent> studentPage =
                classStudentRepository.findPageByClasses_Id(id, pageable);

        List<StudentResponse> students = studentPage.getContent()
                .stream()
                .map(cs -> mapStudent(cs.getStudent()))
                .toList();

        return ClassResponse.builder()
                .id(classes.getId())
                .classroom(classes.getClassroom())
                .name(classes.getName())
                .subjectName(classes.getSubjectName())
                .teacherId(classes.getTeacher().getId())
                .teacherName(classes.getTeacher().getFullName())
                .totalStudents((int) studentPage.getTotalElements())
                .students(students)
                .build();
    }


    private StudentResponse mapStudent(Student s) {
        return StudentResponse.builder()
                .id(s.getId())
                .studentCode(s.getStudentCode())
                .fullName(s.getFullName())
                .build();
    }
    //UPDATE

    // Teacher
    public ClassResponse updateForTeacher(Long id, ClassRequest request) {

        // check quyền bằng query (chuẩn)
        Classes classes = getClassForTeacher(id);

        classes.setName(request.getName());
        classes.setClassroom(request.getClassroom());
        classes.setSubjectName(request.getSubjectName());

        classesRepository.save(classes);

        return mapToResponse(classes);
    }

    // Admin
    public ClassResponse updateForAdmin(Long id, ClassRequest request) {

        Classes classes = getClassOrThrow(id);

        classes.setName(request.getName());
        classes.setClassroom(request.getClassroom());
        classes.setSubjectName(request.getSubjectName());

        //admin có thể đổi teacher
        if (request.getTeacherId() != null) {
            Teacher teacher = teacherRepository.findById(request.getTeacherId())
                    .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy teacher"));

            classes.setTeacher(teacher);
        }

        classesRepository.save(classes);

        return mapToResponse(classes);
    }

    private ClassResponse mapToResponse(Classes c) {
        int total = classStudentRepository.countByClasses_Id(c.getId());

        return ClassResponse.builder()
                .id(c.getId())
                .name(c.getName())
                .classroom(c.getClassroom())
                .subjectName(c.getSubjectName())
                .teacherId(c.getTeacher().getId())
                .teacherName(c.getTeacher().getFullName())
                .totalStudents(total)
                .build();
    }
}