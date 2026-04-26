package com.example.cdtn.service;

import com.example.cdtn.dto.response.FaceResponse;
import com.example.cdtn.dto.response.FaceVerifyResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class FaceApiService {

    private final RestTemplate restTemplate = new RestTemplate();

    public String registerFace(String imageBase64) {

        String url = "https://face-api-production-fccc.up.railway.app/face/register";

        Map<String, String> body = new HashMap<>();
        body.put("image", imageBase64);

        ResponseEntity<FaceResponse> response =
                restTemplate.postForEntity(url, body, FaceResponse.class);

        List<Double> embeddingList = response.getBody().getEmbedding();

        return embeddingList.toString(); // lưu DB dạng string
    }

    public FaceVerifyResponse verifyFace(String image, String embedding) {

        String url = "https://face-api-production-fccc.up.railway.app/face/verify";

        Map<String, Object> body = new HashMap<>();
        body.put("image", image);

        // convert string → list
        body.put("embedding", convertStringToList(embedding));

        ResponseEntity<FaceVerifyResponse> response =
                restTemplate.postForEntity(url, body, FaceVerifyResponse.class);
        if (response.getBody() == null) {
            throw new RuntimeException("AI response null");
        }

        return response.getBody();
    }

    private List<Double> convertStringToList(String embedding) {

        String clean = embedding.replace("[", "").replace("]", "");
        String[] parts = clean.split(",");

        List<Double> list = new ArrayList<>();

        for (String p : parts) {
            list.add(Double.parseDouble(p.trim()));
        }

        return list;
    }
}
