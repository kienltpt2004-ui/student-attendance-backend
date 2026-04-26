package com.example.cdtn.repository;

import com.example.cdtn.entity.Session;
import com.example.cdtn.entity.enums.SessionStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface SessionRepository extends JpaRepository<Session, Long> {
    List<Session> findByClasses_Id(Long classId);
    List<Session> findByStatusAndEndTimeBefore(SessionStatus status, LocalDateTime time);
    Optional<Session> findByIdAndClasses_Teacher_Id(Long id, Long teacherId);
}
