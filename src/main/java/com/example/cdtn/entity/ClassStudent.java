package com.example.cdtn.entity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok .*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "class_students",
        uniqueConstraints = @UniqueConstraint(columnNames = {"class_id", "student_id"})
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ClassStudent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "class_id", nullable = false)
    @JsonIgnore
    private Classes classes;

    @ManyToOne
    @JoinColumn(name = "student_id", nullable = false)
    @JsonIgnore
    private Student student;

    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;
}