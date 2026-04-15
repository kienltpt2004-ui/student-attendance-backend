package com.example.cdtn.repository;

import com.example.cdtn.entity.Teacher;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TeacherRepository extends JpaRepository<Teacher, Long> {
    boolean existsByTeacherCode(String teacherCode);
}
