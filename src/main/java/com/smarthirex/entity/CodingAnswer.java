package com.smarthirex.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "coding_answers")
public class CodingAnswer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;
    private Long questionId;
    private Long jobId;

    @Column(length = 5000)
    private String code;

    private String language;
    private LocalDateTime submittedAt;

    @PrePersist
    public void prePersist() {
        this.submittedAt = LocalDateTime.now();
    }
}