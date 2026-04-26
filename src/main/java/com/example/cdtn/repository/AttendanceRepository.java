package com.example.cdtn.repository;

import com.example.cdtn.entity.Attendance;
import com.example.cdtn.entity.Session;
import com.example.cdtn.entity.Student;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AttendanceRepository extends JpaRepository<Attendance, Long> {
    boolean existsByStudentAndSession(Student student, Session session);

    List<Attendance> findBySession(Session session);

    List<Attendance> findByStudent(Student student);

    List<Attendance> findBySession_Classes_Id(Long classId);
    Page<Attendance> findByStudent_IdAndSession_Classes_Teacher_Id(
            Long studentId,
            Long teacherId,
            Pageable pageable
    );

    Page<Attendance> findByStudent_Id(Long id, Pageable pageable);
}
