package com.example.cdtn.dto.request;

import lombok.*;
import jakarta.validation.constraints.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ClassRequest {
    @NotBlank(message = "Tên lớp không được để trống")
    @Size(max = 100, message = "Tên lớp tối đa 100 ký tự")
    private String name;

    @NotBlank(message = "Tên môn học không được để trống")
    @Size(max = 100, message = "Tên môn học tối đa 100 ký tự")
    private String subjectName;

    @NotNull(message = "TeacherId không được để trống")
    private Long teacherId;
}
