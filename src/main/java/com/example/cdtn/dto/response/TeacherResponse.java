package com.example.cdtn.dto.response;
import com.example.cdtn.entity.enums.Gender;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TeacherResponse {
    private Long id;
    private String teacherCode;
    private String fullName;
    private String phone;
    private Gender gender;
    private LocalDate dateOfBirth;
    private String email;
}
