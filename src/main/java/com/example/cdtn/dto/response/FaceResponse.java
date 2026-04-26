package com.example.cdtn.dto.response;

import lombok.Data;

import java.util.List;

@Data
public class FaceResponse {
    private List<Double> embedding;
}
