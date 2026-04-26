package com.example.cdtn.dto.response;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ClassResponse {
    private Long id;
    private String classroom;
    private String name;
    private String subjectName;
    private Long teacherId;
    private String teacherName;

    private Integer totalStudents;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<StudentResponse> students;
}
