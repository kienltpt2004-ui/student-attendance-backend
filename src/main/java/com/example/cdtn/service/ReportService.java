package com.example.cdtn.service;

import com.example.cdtn.dto.response.report.*;
import com.example.cdtn.entity.*;
import com.example.cdtn.exception.BadRequestException;
import com.example.cdtn.exception.ResourceNotFoundException;
import com.example.cdtn.repository.*;
import com.example.cdtn.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReportService {

    private final AttendanceRepository attendanceRepo;
    private final StudentRepository studentRepo;
    private final SessionRepository sessionRepo;
    private final ClassStudentRepository classStudentRepo;
    private final TeacherRepository teacherRepository;
    private final ClassesRepository classesRepository;

    //CURRENT TEACHER
    private Teacher getCurrentTeacher() {
        var auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !(auth.getPrincipal() instanceof CustomUserDetails user)) {
            throw new BadRequestException("Chưa đăng nhập");
        }

        return teacherRepository.findByUser_Id(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy teacher"));
    }

    //SESSION REPORT
    public List<SessionAttendanceResponse> getSessionReport(Long sessionId) {

        Teacher teacher = getCurrentTeacher();

        Session session = sessionRepo.findById(sessionId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy session"));

        if (!session.getClasses().getTeacher().getId().equals(teacher.getId())) {
            throw new BadRequestException("Không có quyền xem session này");
        }

        List<Attendance> attendances = attendanceRepo.findBySession(session);

        return attendances.stream().map(a -> {
            SessionAttendanceResponse res = new SessionAttendanceResponse();
            res.setStudentId(a.getStudent().getId());
            res.setStudentName(a.getStudent().getFullName());
            res.setStatus(a.getStatus());
            res.setCheckInTime(a.getCheckInTime());
            res.setConfidenceScore(a.getConfidenceScore());
            return res;
        }).toList();
    }

    //STUDENT HISTORY
    public Page<StudentAttendanceResponse> getStudentHistory(Long studentId, int page, int size) {

        Teacher teacher = getCurrentTeacher();
        Pageable pageable = PageRequest.of(page, size);
        Student student = studentRepo.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy sinh viên"));

        Page<Attendance> attendances =
                attendanceRepo.findByStudent_IdAndSession_Classes_Teacher_Id(
                        studentId,
                        teacher.getId(),
                        pageable
                );

        return attendances.map(a -> {
            StudentAttendanceResponse res = new StudentAttendanceResponse();
            res.setClassId(a.getSession().getClasses().getId());
            res.setClassName(a.getSession().getClasses().getName());
            res.setSessionId(a.getSession().getId());
            res.setSessionTitle(a.getSession().getTitle());
            res.setSessionStartTime(a.getSession().getStartTime());
            res.setStatus(a.getStatus());
            res.setCheckInTime(a.getCheckInTime());
            return res;
        });
    }

    //CLASS MATRIX
    public List<ClassMatrixResponse> getClassMatrix(Long classId) {

        Teacher teacher = getCurrentTeacher();

        Classes classes = classesRepository
                .findByIdAndTeacher_Id(classId, teacher.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Không có quyền hoặc không tồn tại lớp"));

        List<ClassStudent> classStudents =
                classStudentRepo.findAllByClasses_Id(classes.getId());

        List<Session> sessions = sessionRepo.findByClasses_Id(classes.getId())
                .stream()
                .sorted(Comparator.comparing(Session::getStartTime))
                .toList();

        Map<Long, Integer> sessionOrderMap = new HashMap<>();
        for (int i = 0; i < sessions.size(); i++) {
            sessionOrderMap.put(sessions.get(i).getId(), i + 1);
        }

        List<Attendance> attendances =
                attendanceRepo.findBySession_Classes_Id(classes.getId());

        Map<String, Attendance> attendanceMap = attendances.stream()
                .collect(Collectors.toMap(
                        a -> a.getStudent().getId() + "_" + a.getSession().getId(),
                        a -> a
                ));

        List<ClassMatrixResponse> result = new ArrayList<>();

        for (ClassStudent cs : classStudents) {

            Student student = cs.getStudent();

            ClassMatrixResponse res = new ClassMatrixResponse();
            res.setStudentId(student.getId());
            res.setStudentName(student.getFullName());

            Map<String, String> map = new LinkedHashMap<>();

            for (Session session : sessions) {

                String key = student.getId() + "_" + session.getId();
                Attendance a = attendanceMap.get(key);

                int order = sessionOrderMap.get(session.getId());
                String date = session.getStartTime()
                        .format(DateTimeFormatter.ofPattern("dd/MM"));

                String label = "Buổi " + order + " (" + date + ")";

                map.put(label, a != null ? a.getStatus().name() : "ABSENT");
            }

            res.setAttendanceMap(map);
            result.add(res);
        }

        return result;
    }

    //MY ATTENDANCE
    public Page<StudentAttendanceResponse> getMyAttendance(String email, int page, int size) {

        Student student = studentRepo.findByUser_Email(email)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy sinh viên"));
        Pageable pageable = PageRequest.of(page, size);

        Page<Attendance> attendances =
                attendanceRepo.findByStudent_Id(student.getId(), pageable);

        return attendances.map(a -> {
            StudentAttendanceResponse res = new StudentAttendanceResponse();
            res.setClassId(a.getSession().getClasses().getId());
            res.setClassName(a.getSession().getClasses().getName());
            res.setSessionId(a.getSession().getId());
            res.setSessionTitle(a.getSession().getTitle());
            res.setSessionStartTime(a.getSession().getStartTime());
            res.setStatus(a.getStatus());
            res.setCheckInTime(a.getCheckInTime());
            return res;
        });
    }

    //EXPORT CLASS MATRIX
    public ByteArrayResource exportClassMatrixExcel(Long classId) {

        List<ClassMatrixResponse> data = getClassMatrix(classId);

        if (data.isEmpty()) {
            throw new RuntimeException("Không có dữ liệu");
        }

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Class Attendance");

        CellStyle headerStyle = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        headerStyle.setFont(font);

        Row header = sheet.createRow(0);
        header.createCell(0).setCellValue("Student Name");

        List<String> sessions = new ArrayList<>(data.get(0).getAttendanceMap().keySet());

        for (int i = 0; i < sessions.size(); i++) {
            Cell cell = header.createCell(i + 1);
            cell.setCellValue(sessions.get(i));
            cell.setCellStyle(headerStyle);
        }

        int rowIndex = 1;

        for (ClassMatrixResponse student : data) {
            Row row = sheet.createRow(rowIndex++);
            row.createCell(0).setCellValue(student.getStudentName());

            int col = 1;
            for (String s : sessions) {
                row.createCell(col++).setCellValue(student.getAttendanceMap().get(s));
            }
        }

        for (int i = 0; i <= sessions.size(); i++) {
            sheet.autoSizeColumn(i);
        }

        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            workbook.write(out);
            workbook.close();
            return new ByteArrayResource(out.toByteArray());
        } catch (Exception e) {
            throw new RuntimeException("Export lỗi");
        }
    }

    //EXPORT SESSION
    public ByteArrayResource exportSessionExcel(Long sessionId) {

        List<SessionAttendanceResponse> data = getSessionReport(sessionId);

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Session Report");

        Row header = sheet.createRow(0);
        header.createCell(0).setCellValue("Student");
        header.createCell(1).setCellValue("Status");
        header.createCell(2).setCellValue("Check-in");
        header.createCell(3).setCellValue("Confidence");

        int rowIndex = 1;

        for (SessionAttendanceResponse s : data) {
            Row row = sheet.createRow(rowIndex++);
            row.createCell(0).setCellValue(s.getStudentName());
            row.createCell(1).setCellValue(s.getStatus().name());
            row.createCell(2).setCellValue(
                    s.getCheckInTime() != null ? s.getCheckInTime().toString() : ""
            );
            row.createCell(3).setCellValue(
                    s.getConfidenceScore() != null ? s.getConfidenceScore() : 0
            );
        }

        for (int i = 0; i < 4; i++) {
            sheet.autoSizeColumn(i);
        }

        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            workbook.write(out);
            workbook.close();
            return new ByteArrayResource(out.toByteArray());
        } catch (Exception e) {
            throw new RuntimeException("Export session lỗi");
        }
    }

    public List<RecentAttendanceResponse> getRecentAttendancesByClass(Long classId) {

        Teacher teacher = getCurrentTeacher();

        // check quyền
        classesRepository.findByIdAndTeacher_Id(classId, teacher.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Không có quyền hoặc lớp không tồn tại"));

        List<Attendance> attendances =
                attendanceRepo
                        .findTop5BySession_Classes_IdAndSession_Classes_Teacher_IdOrderByCheckInTimeDesc(
                                classId,
                                teacher.getId()
                        );

        return attendances.stream().map(a -> {
            RecentAttendanceResponse res = new RecentAttendanceResponse();
            res.setStudentName(a.getStudent().getFullName());
            res.setClassName(a.getSession().getClasses().getName());
            res.setSessionTitle(a.getSession().getTitle());
            res.setCheckInTime(a.getCheckInTime());
            res.setStatus(a.getStatus().name());
            return res;
        }).toList();
    }

    public WeeklyStatsResponse getWeeklyStats() {

        Teacher teacher = getCurrentTeacher();

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime oneWeekAgo = now.minusWeeks(1);

        // Lấy tất cả lớp của teacher
        List<Classes> classes = classesRepository.findByTeacher_Id(teacher.getId());
        List<Long> classIds = classes.stream().map(Classes::getId).toList();

        // Lấy session trong 1 tuần
        List<Session> sessions = sessionRepo
                .findByClasses_IdInAndStartTimeBetween(classIds, oneWeekAgo, now);

        List<Long> sessionIds = sessions.stream().map(Session::getId).toList();

        // Lấy attendance
        List<Attendance> attendances = sessionIds.isEmpty()
                ? new ArrayList<>()
                : attendanceRepo.findBySession_IdIn(sessionIds);

        long totalClasses = classes.size();

        long totalStudents =
                classStudentRepo.countDistinctStudentsByClassIds(classIds);

        long totalSessions = sessions.size();

        long totalAttendances = attendances.size();

        long totalAbsent = attendances.stream()
                .filter(a -> a.getStatus().name().equals("ABSENT"))
                .count();

        double attendanceRate = totalAttendances == 0 ? 0 :
                ((double) (totalAttendances - totalAbsent) / totalAttendances) * 100;

        WeeklyStatsResponse res = new WeeklyStatsResponse();
        res.setTotalClasses(totalClasses);
        res.setTotalStudents(totalStudents);
        res.setTotalSessions(totalSessions);
        res.setTotalAttendances(totalAttendances);
        res.setTotalAbsent(totalAbsent);
        res.setAttendanceRate(attendanceRate);

        return res;
    }

    public ClassStatsResponse getWeeklyStatsByClass(Long classId) {

        Teacher teacher = getCurrentTeacher();

        // check quyền
        Classes classes = classesRepository
                .findByIdAndTeacher_Id(classId, teacher.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Không có quyền hoặc lớp không tồn tại"));

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime oneWeekAgo = now.minusWeeks(1);

        // sessions trong tuần
        List<Session> sessions = sessionRepo
                .findByClasses_IdAndStartTimeBetween(classId, oneWeekAgo, now);

        List<Long> sessionIds = sessions.stream().map(Session::getId).toList();

        // attendance
        List<Attendance> attendances = sessionIds.isEmpty()
                ? new ArrayList<>()
                : attendanceRepo.findBySession_IdIn(sessionIds);

        long totalStudents =
                classStudentRepo.countDistinctStudentsByClassIds(List.of(classId));

        long totalSessions = sessions.size();

        long totalAttendances = attendances.size();

        long totalAbsent = attendances.stream()
                .filter(a -> a.getStatus().name().equals("ABSENT"))
                .count();

        double attendanceRate = totalAttendances == 0 ? 0 :
                ((double) (totalAttendances - totalAbsent) / totalAttendances) * 100;

        ClassStatsResponse res = new ClassStatsResponse();
        res.setTotalStudents(totalStudents);
        res.setTotalSessions(totalSessions);
        res.setTotalAttendances(totalAttendances);
        res.setTotalAbsent(totalAbsent);
        res.setAttendanceRate(attendanceRate);

        return res;
    }
// ADMIN REPORT
//  ADMIN - THỐNG KÊ TỔNG QUAN HỆ THỐNG
    private Classes getClassOrThrow(Long classId) {

        return classesRepository.findById(classId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Không tìm thấy lớp"));
    }
    public WeeklyStatsResponse getSystemStatsForAdmin() {

        List<Classes> classes = classesRepository.findAll();
        List<Long> classIds = classes.stream().map(Classes::getId).toList();

        List<Session> sessions = classIds.isEmpty()
                ? new ArrayList<>()
                : sessionRepo.findByClasses_IdIn(classIds);

        List<Long> sessionIds = sessions.stream()
                .map(Session::getId)
                .toList();

        List<Attendance> attendances = sessionIds.isEmpty()
                ? new ArrayList<>()
                : attendanceRepo.findBySession_IdIn(sessionIds);

        long totalClasses = classes.size();

        long totalStudents =
                classStudentRepo.countDistinctStudentsByClassIds(classIds);

        long totalSessions = sessions.size();

        long totalAttendances = attendances.size();

        long totalAbsent = attendances.stream()
                .filter(a -> a.getStatus().name().equals("ABSENT"))
                .count();

        double attendanceRate = totalAttendances == 0
                ? 0
                : ((double) (totalAttendances - totalAbsent)
                / totalAttendances) * 100;

        WeeklyStatsResponse res = new WeeklyStatsResponse();
        res.setTotalClasses(totalClasses);
        res.setTotalStudents(totalStudents);
        res.setTotalSessions(totalSessions);
        res.setTotalAttendances(totalAttendances);
        res.setTotalAbsent(totalAbsent);
        res.setAttendanceRate(attendanceRate);

        return res;
    }

//    ADMIN - THỐNG KÊ 1 LỚP
    public ClassStatsResponse getClassStatsForAdmin(Long classId) {

        Classes classes = getClassOrThrow(classId);

        List<Session> sessions =
                sessionRepo.findByClasses_Id(classes.getId());

        List<Long> sessionIds = sessions.stream()
                .map(Session::getId)
                .toList();

        List<Attendance> attendances = sessionIds.isEmpty()
                ? new ArrayList<>()
                : attendanceRepo.findBySession_IdIn(sessionIds);

        long totalStudents =
                classStudentRepo.countDistinctStudentsByClassIds(
                        List.of(classId)
                );

        long totalSessions = sessions.size();

        long totalAttendances = attendances.size();

        long totalAbsent = attendances.stream()
                .filter(a -> a.getStatus().name().equals("ABSENT"))
                .count();

        double attendanceRate = totalAttendances == 0
                ? 0
                : ((double) (totalAttendances - totalAbsent)
                / totalAttendances) * 100;

        ClassStatsResponse res = new ClassStatsResponse();
        res.setTotalStudents(totalStudents);
        res.setTotalSessions(totalSessions);
        res.setTotalAttendances(totalAttendances);
        res.setTotalAbsent(totalAbsent);
        res.setAttendanceRate(attendanceRate);

        return res;
    }

//    ADMIN - XEM REPORT 1 SESSION

    public List<SessionAttendanceResponse> getSessionReportForAdmin(Long sessionId) {

        Session session = sessionRepo.findById(sessionId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Không tìm thấy session"));

        List<Attendance> attendances =
                attendanceRepo.findBySession(session);

        return attendances.stream().map(a -> {

            SessionAttendanceResponse res =
                    new SessionAttendanceResponse();

            res.setStudentId(a.getStudent().getId());
            res.setStudentName(a.getStudent().getFullName());
            res.setStatus(a.getStatus());
            res.setCheckInTime(a.getCheckInTime());
            res.setConfidenceScore(a.getConfidenceScore());

            return res;

        }).toList();
    }


//    ADMIN - MA TRẬN ĐIỂM DANH TOÀN LỚP
    public List<ClassMatrixResponse> getClassMatrixForAdmin(Long classId) {

        Classes classes = getClassOrThrow(classId);

        List<ClassStudent> classStudents =
                classStudentRepo.findAllByClasses_Id(classes.getId());

        List<Session> sessions =
                sessionRepo.findByClasses_Id(classes.getId())
                        .stream()
                        .sorted(Comparator.comparing(Session::getStartTime))
                        .toList();

        Map<Long, Integer> sessionOrderMap = new HashMap<>();

        for (int i = 0; i < sessions.size(); i++) {
            sessionOrderMap.put(sessions.get(i).getId(), i + 1);
        }

        List<Attendance> attendances =
                attendanceRepo.findBySession_Classes_Id(classes.getId());

        Map<String, Attendance> attendanceMap =
                attendances.stream()
                        .collect(Collectors.toMap(
                                a -> a.getStudent().getId()
                                        + "_"
                                        + a.getSession().getId(),
                                a -> a
                        ));

        List<ClassMatrixResponse> result = new ArrayList<>();

        for (ClassStudent cs : classStudents) {

            Student student = cs.getStudent();

            ClassMatrixResponse res = new ClassMatrixResponse();

            res.setStudentId(student.getId());
            res.setStudentName(student.getFullName());

            Map<String, String> map = new LinkedHashMap<>();

            for (Session session : sessions) {

                String key =
                        student.getId()
                                + "_"
                                + session.getId();

                Attendance a = attendanceMap.get(key);

                int order =
                        sessionOrderMap.get(session.getId());

                String date = session.getStartTime()
                        .format(DateTimeFormatter.ofPattern("dd/MM"));

                String label =
                        "Buổi " + order + " (" + date + ")";

                map.put(
                        label,
                        a != null
                                ? a.getStatus().name()
                                : "ABSENT"
                );
            }

            res.setAttendanceMap(map);

            result.add(res);
        }

        return result;
    }

//     ADMIN - LỊCH SỬ ĐIỂM DANH 1 SINH VIÊN

    public Page<StudentAttendanceResponse> getStudentHistoryForAdmin(
            Long studentId,
            int page,
            int size
    ) {

        Pageable pageable = PageRequest.of(page, size);

        studentRepo.findById(studentId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Không tìm thấy sinh viên"));

        Page<Attendance> attendances =
                attendanceRepo.findByStudent_Id(studentId, pageable);

        return attendances.map(a -> {

            StudentAttendanceResponse res =
                    new StudentAttendanceResponse();

            res.setClassId(a.getSession().getClasses().getId());
            res.setClassName(a.getSession().getClasses().getName());

            res.setSessionId(a.getSession().getId());
            res.setSessionTitle(a.getSession().getTitle());

            res.setSessionStartTime(
                    a.getSession().getStartTime()
            );

            res.setStatus(a.getStatus());

            res.setCheckInTime(a.getCheckInTime());

            return res;
        });
    }
}