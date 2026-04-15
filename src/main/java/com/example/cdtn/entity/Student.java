package com.example.cdtn.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "students")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Student {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "student_code", nullable = false, unique = true, length = 50)
    private String studentCode;

    @Column(name = "full_name", nullable = false, length = 100)
    private String fullName;

    @Column(nullable = false)
    private Integer age;

    @Column(nullable = false, length = 100)
    private String department;

    // ĐÃ THÊM: đánh dấu sinh viên đã đăng ký khuôn mặt hay chưa
    @Column(name = "face_registered", nullable = false)
    private Boolean faceRegistered = false;

    // ĐÃ THÊM: lưu đường dẫn ảnh khuôn mặt gốc của sinh viên
    @Column(name = "face_image_url", length = 255)
    private String faceImageUrl;

    @Column(name = "face_embedding", columnDefinition = "TEXT")
    private String faceEmbedding;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    @JsonIgnore
    private User user;

    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}