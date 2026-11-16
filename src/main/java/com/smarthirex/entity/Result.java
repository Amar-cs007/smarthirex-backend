package com.smarthirex.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "results")
public class Result {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;
    private Long jobId;
    private int score;
    private int totalQuestions;
    private LocalDateTime attemptedAt;

    @PrePersist
    public void prePersist() {
        this.attemptedAt = LocalDateTime.now();
    }
}
