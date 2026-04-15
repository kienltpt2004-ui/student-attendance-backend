package com.example.cdtn.dto.response;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StudentResponse {
    private Long id;
    private String studentCode;
    private String fullName;
    private Integer age;
    private String department;
    private String email;
}
