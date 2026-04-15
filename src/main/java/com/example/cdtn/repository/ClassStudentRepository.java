package com.example.cdtn.repository;

import com.example.cdtn.entity.ClassStudent;
import com.example.cdtn.entity.Classes;
import com.example.cdtn.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ClassStudentRepository extends JpaRepository<ClassStudent, Long> {

    boolean existsByClassesAndStudent(Classes classes, Student student);

    Optional<ClassStudent> findByClasses_IdAndStudent_Id(Long classId, Long studentId);

    int countByClasses_Id(Long classId);
}
