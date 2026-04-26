package com.example.cdtn.dto.response.report;
import com.example.cdtn.entity.enums.AttendanceStatus;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
public class StudentAttendanceResponse {
    private Long classId;
    private String className;
    private Long sessionId;
    private String sessionTitle;
    private LocalDateTime sessionStartTime;
    private AttendanceStatus status;
    private LocalDateTime checkInTime;
}
