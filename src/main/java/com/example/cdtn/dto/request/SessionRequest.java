package com.example.cdtn.dto.request;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
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

    @NotNull(message = "Latitude không được null")
    @DecimalMin(value = "-90.0", message = "Latitude không hợp lệ")
    @DecimalMax(value = "90.0", message = "Latitude không hợp lệ")
    private Double latitude;

    @NotNull(message = "Longitude không được null")
    @DecimalMin(value = "-180.0", message = "Longitude không hợp lệ")
    @DecimalMax(value = "180.0", message = "Longitude không hợp lệ")
    private Double longitude;

    @NotNull(message = "Radius không được để trống")
    @DecimalMin(value = "0", message = "Radius phải >= 0m")
    @DecimalMax(value = "50.0", message = "Radius không được vượt quá 50m")
    private Double radius;
}
