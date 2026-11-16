package com.smarthirex.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "resume_screenings")
public class ResumeScreening {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;
    private Long jobId;

    @Column(length = 5000)
    private String resumeText;

    private int aiScore;

    @Column(length = 1000)
    private String aiFeedback;

    private String status; // SELECTED, REJECTED

    private LocalDateTime screenedAt;

    @PrePersist
    public void prePersist() {
        this.screenedAt = LocalDateTime.now();
    }
}
