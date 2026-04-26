package com.example.cdtn.dto.request;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
public class AttendanceRequest {

//    @NotNull(message = "studentId không được null")
//    private Long studentId;

    @NotNull(message = "sessionId không được null")
    private Long sessionId;

    @NotBlank(message = "Ảnh không được để trống")
    private String image;

    @NotNull(message = "Latitude không được null")
    @DecimalMin(value = "-90.0", message = "Latitude không hợp lệ")
    @DecimalMax(value = "90.0", message = "Latitude không hợp lệ")
    private Double latitude;

    @NotNull(message = "Longitude không được null")
    @DecimalMin(value = "-180.0", message = "Longitude không hợp lệ")
    @DecimalMax(value = "180.0", message = "Longitude không hợp lệ")
    private Double longitude;
}
