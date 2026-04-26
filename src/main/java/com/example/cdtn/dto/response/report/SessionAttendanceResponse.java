package com.example.cdtn.dto.response.report;

import com.example.cdtn.entity.enums.AttendanceStatus;
import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
public class SessionAttendanceResponse {
    private Long studentId;
    private String studentName;
    private AttendanceStatus status;
    private LocalDateTime checkInTime;
    private Double confidenceScore;
}
