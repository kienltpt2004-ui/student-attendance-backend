package com.example.cdtn.repository;

import com.example.cdtn.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface StudentRepository extends JpaRepository<Student, Long> {
    boolean existsByStudentCode(String studentCode);
    Optional<Student> findByStudentCode(String studentCode);
    boolean existsByPhone(String phone);
    Optional<Student> findByUser_Email(String email);

    List<Student> findByFaceRegisteredTrueAndIdNot(Long studentId);

    Optional<Student> findByUser_Id(Long userId);

    List<Student> findByFaceRegisteredTrue();
}

