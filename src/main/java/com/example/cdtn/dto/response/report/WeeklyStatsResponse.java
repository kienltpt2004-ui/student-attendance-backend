package com.example.cdtn.dto.response.report;
import lombok.*;

@Getter
@Setter
public class WeeklyStatsResponse {

    private long totalClasses;
    private long totalStudents;
    private long totalSessions;
    private long totalAttendances;
    private long totalAbsent;
    private double attendanceRate;
}
