package com.example.cdtn.dto.response.report;
import lombok.*;

import java.util.Map;

@Getter
@Setter
public class ClassMatrixResponse {
    private Long studentId;
    private String studentName;

    private Map<String, String> attendanceMap;
}
