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


    private Teacher getCurrentTeacher() {
        var auth = SecurityContextHolder.getContext().getAuthentication();

        if (!(auth.getPrincipal() instanceof CustomUserDetails user)) {
            throw new BadRequestException("Chưa đăng nhập");
        }

        return teacherRepository.findByUser_Id(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy teacher"));
    }


    private Classes getClassForTeacher(Long classId) {
        return classesRepository
                .findByIdAndTeacher_Id(classId, getCurrentTeacher().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Không có quyền"));
    }

    private Classes getClassOrThrow(Long classId) { // ADMIN dùng
        return classesRepository.findById(classId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy lớp"));
    }

    //TEACHER
    public List<SessionResponse> getMySessions(Long classId) {
        Classes classes = getClassForTeacher(classId);
        return getByClass(classes.getId());
    }

    //ADMIN
    public List<SessionResponse> getAllSessions(Long classId) {
        Classes classes = getClassOrThrow(classId);
        return getByClass(classes.getId());
    }

    private List<SessionResponse> getByClass(Long classId) {
        return sessionRepository.findByClasses_Id(classId)
                .stream()
                .map(this::map)
                .toList();
    }
    //GET BY ID

    // Teacher
    public SessionResponse getByIdForTeacher(Long id) {

        Teacher teacher = getCurrentTeacher();

        //check quyền ngay từ query
        Session s = sessionRepository
                .findByIdAndClasses_Teacher_Id(id, teacher.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Không có quyền hoặc không tồn tại"));

        return map(s);
    }
    // Admin
    public SessionResponse getByIdForAdmin(Long id) {

        Session s = sessionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy session"));

        return map(s);
    }

    //CREATE
    public SessionResponse createForTeacher(SessionRequest req) {

        Classes classes = getClassForTeacher(req.getClassId()); //đã check quyền

        if (req.getEndTime().isBefore(req.getStartTime())) {
            throw new BadRequestException("EndTime phải sau StartTime");
        }

        Session s = new Session();
        s.setClasses(classes);
        s.setTitle(req.getTitle());
        s.setStartTime(req.getStartTime());
        s.setEndTime(req.getEndTime());
        s.setLatitude(req.getLatitude());
        s.setLongitude(req.getLongitude());
        s.setRadius(req.getRadius());
        s.setStatus(SessionStatus.OPEN);

        sessionRepository.save(s);
        return map(s);
    }

    //CLOSE
    public void close(Long id) {

        Teacher teacher = getCurrentTeacher(); // NEW

        //check quyền ngay từ query
        Session s = sessionRepository
                .findByIdAndClasses_Teacher_Id(id, teacher.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Không có quyền hoặc không tồn tại"));

        if (s.getStatus() == SessionStatus.CLOSED) {
            throw new BadRequestException("Session đã đóng rồi");
        }

        s.setStatus(SessionStatus.CLOSED);

        attendanceService.generateAbsent(s);

        sessionRepository.save(s);
    }
    //CLOSE (ADMIN)

    public void closeForAdmin(Long id) {

        // ADMIN không check teacher
        Session s = sessionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy session"));

        // validate đã đóng chưa
        if (s.getStatus() == SessionStatus.CLOSED) {
            throw new BadRequestException("Session đã đóng rồi");
        }

        s.setStatus(SessionStatus.CLOSED);

        // generate absent
        attendanceService.generateAbsent(s);

        sessionRepository.save(s);
    }
    //CREATE (ADMIN)

    public SessionResponse createForAdmin(SessionRequest req) {

        // ADMIN không check teacher
        Classes classes = getClassOrThrow(req.getClassId());

        // validate thời gian
        if (req.getEndTime().isBefore(req.getStartTime())) {
            throw new BadRequestException("EndTime phải sau StartTime");
        }

        Session s = new Session();
        s.setClasses(classes);
        s.setTitle(req.getTitle());
        s.setStartTime(req.getStartTime());
        s.setEndTime(req.getEndTime());

        // GPS
        s.setLatitude(req.getLatitude());
        s.setLongitude(req.getLongitude());
        s.setRadius(req.getRadius());

        s.setStatus(SessionStatus.OPEN);

        sessionRepository.save(s);

        return map(s);
    }

    private SessionResponse map(Session s) {
        return SessionResponse.builder()
                .id(s.getId())
                .classId(s.getClasses().getId())
                .title(s.getTitle())
                .startTime(s.getStartTime())
                .endTime(s.getEndTime())
                .latitude(s.getLatitude())
                .longitude(s.getLongitude())
                .radius(s.getRadius())
                .status(s.getStatus().name())
                .build();
    }
}