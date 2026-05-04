package com.example.cdtn.dto.request;

import lombok.*;
import jakarta.validation.constraints.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ClassRequest {

    private Long teacherId;
    @NotBlank(message = "Tên phòng không được để trống")
    @Size(max = 15, message = "Tên phòng tối đa 15 ký tự")
    private String classroom;

    @NotBlank(message = "Tên lớp không được để trống")
    @Size(max = 100, message = "Tên lớp tối đa 100 ký tự")
    private String name;

    @NotBlank(message = "Tên môn học không được để trống")
    @Size(max = 100, message = "Tên môn học tối đa 100 ký tự")
    private String subjectName;
}
