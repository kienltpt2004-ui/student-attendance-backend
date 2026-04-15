package com.example.cdtn.dto.request;


import lombok.*;
import jakarta.validation.constraints.*;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StudentRequest {
    @NotBlank(message = "Mã sinh viên không được để trống")
    @Size(max = 50, message = "Mã sinh viên tối đa 50 ký tự")
    private String studentCode;

    @NotBlank(message = "Tên sinh viên không được để trống")
    @Size(max = 100, message = "Tên sinh viên tối đa 100 ký tự")
    private String fullName;

    @NotNull(message = "Tuổi không được dể trống")
    @Min(value = 16, message = "Tuổi phải >= 17")
    @Max(value = 100, message = "Tuổi phải <= 100")
    private Integer age;

    @NotBlank(message = "Khoa không được để trống")
    @Size(max = 100, message = "Tên khoa tối đa 100 ký tự")
    private String department;

    @NotBlank(message = "Email không được để trống")
    @Size(max = 100, message = "Email tối đa 100 ký tự")
    @Pattern(
            regexp = "^A\\d{5}@thanglong\\.edu\\.vn$",
            message = "Email phải có dạng Axxxxx@thanglong.edu.vn"
    )
    private String email;

    @NotBlank(message = "Mật khẩu không được để trống")
    @Size(min = 6, max = 100, message = "Mật khẩu từ 6 đến 100 ký tự")
    private String password;

}
