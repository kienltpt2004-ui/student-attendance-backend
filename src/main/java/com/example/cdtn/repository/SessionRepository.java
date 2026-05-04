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
    List<Session> findByClasses_IdInAndStartTimeBetween(
            List<Long> classIds,
            LocalDateTime from,
            LocalDateTime to
    );
    List<Session> findByClasses_IdAndStartTimeBetween(
            Long classId,
            LocalDateTime from,
            LocalDateTime to
    );

    List<Session> findByClasses_IdIn(List<Long> classIds);
}
