package com.example.cdtn.dto.response.report;
import lombok.*;

@Getter
@Setter
public class ClassStatsResponse {
    private long totalStudents;
    private long totalSessions;
    private long totalAttendances;
    private long totalPresent;
    private long totalAbsent;
    private double attendanceRate;
}
