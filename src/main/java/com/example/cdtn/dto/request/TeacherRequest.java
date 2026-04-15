package com.example.cdtn.dto.request;
import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TeacherRequest {
    @NotBlank(message = "Mã giảng viên không được để trống")
    @Size(max = 50, message = "Mã giảng viên tối đa 50 ký tự")
    private String teacherCode;

    @NotBlank(message = "Tên giảng viên không được để trống")
    @Size(max = 100, message = "Tên giảng viên tối đa 100 ký tự")
    private String fullName;

    @NotNull(message = "Tuổi không được để trống")
    @Min(value = 22, message = "Tuổi phải >= 22")
    @Max(value = 100, message = "Tuổi phải <= 100")
    private Integer age;

    @NotBlank(message = "Email không được để trống")
    @Pattern(
            regexp = "^CT[0-9]+@thanglong\\.edu\\.vn$",
            message = "Email phải có dạng CTxxxx@thanglong.edu.vn"
    )
    private String email;

    @NotBlank(message = "Mật khẩu không được để trống")
    @Size(min = 6, max = 100, message = "Mật khẩu từ 6 đến 100 ký tự")
    private String password;
}
