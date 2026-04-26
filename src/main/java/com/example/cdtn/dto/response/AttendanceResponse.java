package com.example.cdtn.dto.response;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AttendanceResponse {

    private Long id;
    private Long studentId;
    private Long sessionId;

    private String status;
    private Double confidenceScore;

    private String studentName;
    private String sessionTitle;
    private LocalDateTime checkInTime;
}