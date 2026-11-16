package com.smarthirex.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "test_case_results")
public class TestCaseResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;
    private Long questionId;
    private Long testCaseId;

    @Column(length = 5000)
    private String submittedCode;

    private String language;
    private String actualOutput;
    private boolean passed;
    private String status;
    private double executionTime;
}
