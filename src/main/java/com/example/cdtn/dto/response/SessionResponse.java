package com.example.cdtn.dto.response;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SessionResponse {
    private Long id;
    private Long classId;
    private String title;
    private LocalDateTime startTime;
    private LocalDateTime endTime;

    private Double latitude;
    private Double longitude;
    private Double radius;

    private String status;

}
