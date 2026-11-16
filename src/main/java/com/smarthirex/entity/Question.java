package com.smarthirex.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "questions")
public class Question {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 1000)
    private String questionText;

    // MCQ fields
    private String optionA;
    private String optionB;
    private String optionC;
    private String optionD;
    private String correctAnswer;

    // Type: MCQ or CODING
    private String questionType;

    // Coding problem fields
    @Column(length = 2000)
    private String problemDescription;

    @Column(length = 1000)
    private String sampleInput;

    @Column(length = 1000)
    private String sampleOutput;

    @Column(length = 500)
    private String constraints;

    private Long jobId;
}