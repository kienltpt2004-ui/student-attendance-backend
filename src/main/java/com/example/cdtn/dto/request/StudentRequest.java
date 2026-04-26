package com.example.cdtn.dto.request;


import com.example.cdtn.entity.enums.Gender;
import lombok.*;
import jakarta.validation.constraints.*;

import java.time.LocalDate;


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

    @Size(max = 255, message = "Địa chỉ tối đa 255 ký tự")
    private String address;

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
