package com.example.cdtn.dto.response;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TeacherResponse {
    private Long id;
    private String teacherCode;
    private String fullName;
    private Integer age;
    private String email;
}
