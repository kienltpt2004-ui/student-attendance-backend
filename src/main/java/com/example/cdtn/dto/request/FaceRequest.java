package com.example.cdtn.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.*;
@Getter
@Setter
public class FaceRequest {

    @NotBlank(message = "Ảnh không được để trống")
    private String image;
}
