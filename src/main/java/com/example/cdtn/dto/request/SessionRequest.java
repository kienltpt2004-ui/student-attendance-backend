package com.example.cdtn.dto.request;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.aspectj.bridge.IMessage;
import org.springframework.cglib.core.Local;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SessionRequest {

    @NotNull(message = "ClassId không được để trống")
    private Long classId;

    @NotNull(message = "Tiêu đề không được để trống")
    private String title;

    @NotNull(message = "StartTime không được để trống")
    private LocalDateTime startTime;

    @NotNull(message = "EndTime không được để trống")
    private LocalDateTime endTime;

    private Double latitude;
    private Double longitude;
    private Double radius;
}
