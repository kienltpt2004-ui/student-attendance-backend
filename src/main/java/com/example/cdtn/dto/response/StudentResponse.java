package com.example.cdtn.dto.response;
import com.example.cdtn.entity.enums.Gender;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StudentResponse {
    private Long id;
    private String studentCode;
    private String fullName;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String phone;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Gender gender;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private LocalDate dateOfBirth;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String address;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String department;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String email;
}
