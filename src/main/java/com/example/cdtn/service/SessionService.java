package com.example.cdtn.service;

import com.example.cdtn.dto.request.SessionRequest;
import com.example.cdtn.dto.response.SessionResponse;
import com.example.cdtn.entity.*;
import com.example.cdtn.entity.enums.SessionStatus;
import com.example.cdtn.exception.BadRequestException;
import com.example.cdtn.exception.ResourceNotFoundException;
import com.example.cdtn.repository.*;
import com.example.cdtn.security.CustomUserDetails;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SessionService {

    private final SessionRepository sessionRepository;
    private final ClassesRepository classesRepository;
    private final TeacherRepository teacherRepository;
    private final AttendanceService attendanceService;

    public SessionService(SessionRepository sessionRepository,
                          ClassesRepository classesRepository,
                          TeacherRepository teacherRepository,
                          AttendanceService attendanceService) {
        this.sessionRepository = sessionRepository;
        this.classesRepository = classesRepository;
        this.teacherRepository = teacherRepository;
        this.attendanceService = attendanceService;
    }

    //GET CURRENT TEACHER
    private Teacher getCurrentTeacher() {
        var auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !(auth.getPrincipal() instanceof CustomUserDetails user)) {
            throw new BadRequestException("Chưa đăng nhập");
        }

        return teacherRepository.findByUser_Id(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy teacher"));
    }

    //CREATE SESSION
    public SessionResponse createSession(SessionRequest request) {

        Teacher teacher = getCurrentTeacher();

        Classes classes = classesRepository
                .findByIdAndTeacher_Id(request.getClassId(), teacher.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy lớp"));

        if (request.getEndTime().isBefore(request.getStartTime())) {
            throw new BadRequestException("EndTime phải sau StartTime");
        }

        Session session = new Session();
        session.setClasses(classes);
        session.setTitle(request.getTitle());
        session.setStartTime(request.getStartTime());
        session.setEndTime(request.getEndTime());
        session.setLatitude(request.getLatitude());
        session.setLongitude(request.getLongitude());
        session.setRadius(request.getRadius());
        session.setStatus(SessionStatus.OPEN);

        sessionRepository.save(session);

        return mapToResponse(session);
    }

    //GET ALL SESSION BY CLASS
    public List<SessionResponse> getAllSessionByClass(Long classId) {

        Teacher teacher = getCurrentTeacher();

        Classes classes = classesRepository
                .findByIdAndTeacher_Id(classId, teacher.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy lớp"));

        return sessionRepository.findByClasses_Id(classes.getId())
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    //GET SESSION BY ID
    public SessionResponse getSessionById(Long id) {

        Teacher teacher = getCurrentTeacher();

        Session session = sessionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy session"));

        if (!session.getClasses().getTeacher().getId().equals(teacher.getId())) {
            throw new BadRequestException("Không có quyền");
        }

        return mapToResponse(session);
    }

    //CLOSE SESSION
    public void closeSession(Long id) {

        Teacher teacher = getCurrentTeacher();

        Session session = sessionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy session"));

        if (!session.getClasses().getTeacher().getId().equals(teacher.getId())) {
            throw new BadRequestException("Không có quyền đóng session này");
        }

        if (session.getStatus() == SessionStatus.ClOSED) {
            throw new BadRequestException("Session đã đóng rồi");
        }

        session.setStatus(SessionStatus.ClOSED);

        attendanceService.generateAbsent(session);

        sessionRepository.save(session);
    }

    // ===== MAP =====
    private SessionResponse mapToResponse(Session session) {
        return SessionResponse.builder()
                .id(session.getId())
                .classId(session.getClasses().getId())
                .title(session.getTitle())
                .startTime(session.getStartTime())
                .endTime(session.getEndTime())
                .latitude(session.getLatitude())
                .longitude(session.getLongitude())
                .radius(session.getRadius())
                .status(session.getStatus().name())
                .build();
    }
}