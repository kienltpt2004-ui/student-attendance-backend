package com.example.cdtn.repository;

import com.example.cdtn.entity.ClassStudent;
import com.example.cdtn.entity.Classes;
import com.example.cdtn.entity.Student;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ClassStudentRepository extends JpaRepository<ClassStudent, Long> {

    boolean existsByClassesAndStudent(Classes classes, Student student);

    Optional<ClassStudent> findByClasses_IdAndStudent_Id(Long classId, Long studentId);

    int countByClasses_Id(Long classId);
    Page<ClassStudent> findPageByClasses_Id(Long classId, Pageable pageable);
    boolean existsByStudentAndClasses(Student student, Classes classes);

    List<ClassStudent> findByClasses(Classes classes);
    List<ClassStudent> findAllByClasses_Id(Long classId);

    @Query("""
    SELECT COUNT(DISTINCT cs.student.id)
    FROM ClassStudent cs
    WHERE cs.classes.id IN :classIds
    """)
    long countDistinctStudentsByClassIds(List<Long> classIds);
}
