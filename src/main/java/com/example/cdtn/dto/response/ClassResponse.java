package com.example.cdtn.dto.response;
import lombok.*;
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ClassResponse {
    private Long id;
    private String name;
    private String subjectName;
    private Long teacherId;
    private String teacherName;

    private Integer totalStudents;
}
