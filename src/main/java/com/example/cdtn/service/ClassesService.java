package com.example.cdtn.service;

import com.example.cdtn.dto.request.ClassRequest;
import com.example.cdtn.dto.response.ClassResponse;
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
import jakarta.transaction.Transactional;
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

    public ClassResponse createClass(ClassRequest request) {

        Teacher teacher = teacherRepository.findById(request.getTeacherId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy giáo viên"));

        Classes classes = new Classes();
        classes.setName(request.getName());
        classes.setSubjectName(request.getSubjectName());
        classes.setTeacher(teacher);

        classesRepository.save(classes);

        return mapToResponse(classes);
    }

    public List<ClassResponse> getAllClasses() {
        return classesRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    public ClassResponse getClassById(Long id) {
        Classes classes = classesRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy lớp"));
        return mapToResponse(classes);
    }

    @Transactional
    public void addStudent(Long classId, Long studentId) {

        Classes classes = classesRepository.findById(classId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy lớp"));

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

    @Transactional
    public void removeStudent(Long classId, Long studentId) {

        ClassStudent cs = classStudentRepository
                .findByClasses_IdAndStudent_Id(classId, studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Sinh viên không thuộc lớp"));

        classStudentRepository.delete(cs);
    }

    private ClassResponse mapToResponse(Classes classes) {

        int total = classStudentRepository.countByClasses_Id(classes.getId());

        return ClassResponse.builder()
                .id(classes.getId())
                .name(classes.getName())
                .subjectName(classes.getSubjectName())
                .teacherId(classes.getTeacher().getId())
                .teacherName(classes.getTeacher().getFullName())
                .totalStudents(total)
                .build();
    }
}
