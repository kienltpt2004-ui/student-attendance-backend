package com.example.cdtn.repository;

import com.example.cdtn.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudentRepository extends JpaRepository<Student, Long> {
    boolean existsByStudentCode(String studentCode);}
