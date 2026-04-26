package com.example.cdtn.service;

import com.example.cdtn.dto.request.AttendanceRequest;
import com.example.cdtn.dto.response.AttendanceResponse;
import com.example.cdtn.dto.response.FaceVerifyResponse;
import com.example.cdtn.entity.*;
import com.example.cdtn.entity.enums.AttendanceStatus;
import com.example.cdtn.entity.enums.SessionStatus;
import com.example.cdtn.exception.BadRequestException;
import com.example.cdtn.exception.ResourceNotFoundException;
import com.example.cdtn.repository.*;
import com.example.cdtn.security.CustomUserDetails;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AttendanceService {

    private final AttendanceRepository attendanceRepo;
    private final StudentRepository studentRepo;
    private final SessionRepository sessionRepo;
    private final ClassStudentRepository classStudentRepo;
    private final FaceApiService faceApiService;

    public AttendanceService(AttendanceRepository attendanceRepo,
                             StudentRepository studentRepo,
                             SessionRepository sessionRepo,
                             ClassStudentRepository classStudentRepo,
                             FaceApiService faceApiService){
        this.attendanceRepo = attendanceRepo;
        this.studentRepo = studentRepo;
        this.sessionRepo = sessionRepo;
        this.classStudentRepo = classStudentRepo;
        this.faceApiService = faceApiService;
    }
    private Student getCurrentStudent() {
        var auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !(auth.getPrincipal() instanceof CustomUserDetails user)) {
            throw new BadRequestException("Chưa đăng nhập");
        }

        return studentRepo.findByUser_Id(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy sinh viên"));
    }

    public AttendanceResponse checkIn(AttendanceRequest request) {

        // ✅ LẤY STUDENT TỪ TOKEN (QUAN TRỌNG NHẤT)
        Student student = getCurrentStudent();

        Session session = sessionRepo.findById(request.getSessionId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy session"));

        if (student.getFaceEmbedding() == null) {
            throw new BadRequestException("Chưa đăng ký khuôn mặt");
        }

        if (session.getStatus() != SessionStatus.OPEN) {
            throw new BadRequestException("Session đã đóng");
        }

        if (!classStudentRepo.existsByStudentAndClasses(student, session.getClasses())) {
            throw new BadRequestException("Sinh viên không thuộc lớp");
        }

        if (attendanceRepo.existsByStudentAndSession(student, session)) {
            throw new BadRequestException("Đã điểm danh rồi");
        }

        // ===== GPS =====
        double distance = calculateDistance(
                request.getLatitude(),
                request.getLongitude(),
                session.getLatitude(),
                session.getLongitude()
        );

        if (distance > session.getRadius()) {
            throw new BadRequestException("Ngoài phạm vi lớp");
        }

        // ===== FACE =====
        FaceVerifyResponse faceRes = faceApiService.verifyFace(
                request.getImage(),
                student.getFaceEmbedding()
        );

        if (!faceRes.isMatch()) {
            throw new BadRequestException("Face không hợp lệ");
        }

        LocalDateTime now = LocalDateTime.now();

        if (now.isAfter(session.getStartTime().plusMinutes(30))) {
            throw new BadRequestException("Đã quá thời gian điểm danh");
        }

        AttendanceStatus status = now.isAfter(session.getStartTime().plusMinutes(10))
                ? AttendanceStatus.LATE
                : AttendanceStatus.PRESENT;

        Attendance attendance = new Attendance();
        attendance.setStudent(student); // ✅ luôn là chính user
        attendance.setSession(session);
        attendance.setStatus(status);
        attendance.setLatitude(request.getLatitude());
        attendance.setLongitude(request.getLongitude());
        attendance.setCheckInTime(now);

        attendance.setConfidenceScore(1 - faceRes.getDistance());

        Attendance saved = attendanceRepo.save(attendance);

        return mapToResponse(saved);
    }

//    public AttendanceResponse checkIn(AttendanceRequest request) {
//
////        Student student = studentRepo.findById(request.getStudentId())
////                .orElseThrow(() -> new ResourceNotFoundException("không tìm thấy sinh viên"));
//
//        Session session = sessionRepo.findById(request.getSessionId())
//                .orElseThrow(() -> new ResourceNotFoundException("không tìm thấy session"));
//
//        if (student.getFaceEmbedding() == null) {
//            throw new BadRequestException("Chưa đăng ký khuôn mặt");
//        }
//
//        if (session.getStatus() != SessionStatus.OPEN) {
//            throw new BadRequestException("Session đã đóng");
//        }
//
//        if (!classStudentRepo.existsByStudentAndClasses(student, session.getClasses())) {
//            throw new BadRequestException("Sinh viên không thuộc lớp");
//        }
//
//        if (attendanceRepo.existsByStudentAndSession(student, session)) {
//            throw new BadRequestException("Đã điểm danh rồi");
//        }
//
//        // GPS
//        double distance = calculateDistance(
//                request.getLatitude(),
//                request.getLongitude(),
//                session.getLatitude(),
//                session.getLongitude()
//        );
//
//        if (distance > session.getRadius()) {
//            throw new BadRequestException("Ngoài phạm vi lớp");
//        }
//
//        // FACE
//        FaceVerifyResponse faceRes = faceApiService.verifyFace(
//                request.getImage(),
//                student.getFaceEmbedding()
//        );
//
//        if (!faceRes.isMatch()) {
//            throw new BadRequestException("Face không hợp lệ");
//        }
//
//        LocalDateTime now = LocalDateTime.now();
//        if (now.isAfter(session.getStartTime().plusMinutes(30))) {
//            throw new BadRequestException("Đã quá thời gian điểm danh");
//        }
//
//        AttendanceStatus status = now.isAfter(session.getStartTime().plusMinutes(10))
//                ? AttendanceStatus.LATE
//                : AttendanceStatus.PRESENT;
//
//        Attendance attendance = new Attendance();
//        attendance.setStudent(student);
//        attendance.setSession(session);
//        attendance.setStatus(status);
//        attendance.setLatitude(request.getLatitude());
//        attendance.setLongitude(request.getLongitude());
//
//        attendance.setCheckInTime(now);
//
//        attendance.setConfidenceScore(1 - faceRes.getDistance());
//        Attendance saved = attendanceRepo.save(attendance);
//
//        return mapToResponse(saved);
//    }

    //CREATE ABSENT
    public void generateAbsent(Session session) {

        List<ClassStudent> classStudents =
                classStudentRepo.findByClasses(session.getClasses());

        for (ClassStudent cs : classStudents) {

            Student student = cs.getStudent();

            boolean existed =
                    attendanceRepo.existsByStudentAndSession(student, session);

            if (!existed) {
                Attendance absent = new Attendance();
                absent.setStudent(student);
                absent.setSession(session);
                absent.setStatus(AttendanceStatus.ABSENT);

                absent.setCheckInTime(null);
                absent.setLatitude(null);
                absent.setLongitude(null);
                absent.setConfidenceScore(null);

                attendanceRepo.save(absent);
            }
        }
    }

    //GPS CALCULATION
    public double calculateDistance(double lat1, double lon1,
                                    double lat2, double lon2) {

        final int R = 6371000; // mét

        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1))
                * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLon / 2) * Math.sin(dLon / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return R * c;
    }
    private AttendanceResponse mapToResponse(Attendance a) {
        AttendanceResponse res = new AttendanceResponse();

        res.setId(a.getId());
        res.setStudentId(a.getStudent().getId());
        res.setStudentName(a.getStudent().getFullName());
        res.setStatus(a.getStatus().name());
        res.setSessionId(a.getSession().getId());
        res.setSessionTitle(a.getSession().getTitle());
        res.setConfidenceScore(a.getConfidenceScore());
        res.setCheckInTime(a.getCheckInTime());

        return res;
    }
}