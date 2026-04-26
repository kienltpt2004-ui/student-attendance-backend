package com.example.cdtn.dto.response;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class FaceVerifyResponse {
    private boolean match;
    private double distance;
}