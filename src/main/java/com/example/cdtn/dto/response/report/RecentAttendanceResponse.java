package com.example.cdtn.dto.response.report;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
public class RecentAttendanceResponse {
    private String studentName;
    private String className;
    private String sessionTitle;
    private LocalDateTime checkInTime;
    private String status;
}
