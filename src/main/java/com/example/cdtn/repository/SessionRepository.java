package com.example.cdtn.repository;

import com.example.cdtn.entity.Session;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SessionRepository extends JpaRepository<Session, Long> {
    List<Session> findByClasses_Id(Long classId);
}
