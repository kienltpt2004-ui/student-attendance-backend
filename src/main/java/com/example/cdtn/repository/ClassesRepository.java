package com.example.cdtn.repository;

import com.example.cdtn.entity.Classes;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ClassesRepository extends JpaRepository<Classes, Long> {
    boolean existsByName(String name);
    Page<Classes> findByTeacher_Id(Long teacherId, Pageable pageable);
    Optional<Classes> findByIdAndTeacher_Id(Long id, Long teacherId);
}
