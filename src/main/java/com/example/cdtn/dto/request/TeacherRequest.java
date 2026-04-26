package com.example.cdtn.dto.request;
import com.example.cdtn.entity.enums.Gender;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;

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

    @Pattern(
            regexp = "^(0|\\+84)\\d{9}$",
            message = "Số điện thoại không hợp lệ"
    )
    private String phone;

    @NotNull(message = "Giới tính không được để trống")
    private Gender gender;

    @NotNull(message = "Ngày sinh không được để trống")
    @Past(message = "Ngày sinh phải là ngày trong quá khứ")
    private LocalDate dateOfBirth;

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
