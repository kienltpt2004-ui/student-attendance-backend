package com.example.cdtn.repository;

import com.example.cdtn.entity.Teacher;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TeacherRepository extends JpaRepository<Teacher, Long> {
    boolean existsByTeacherCode(String teacherCode);
    Optional<Teacher> findByTeacherCode(String code);
    boolean existsByPhone(String phone);
    Optional<Teacher> findByUser_Id(Long userId);
}
