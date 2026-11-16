package com.smarthirex.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "test_cases")
public class TestCase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long questionId;

    @Column(length = 1000)
    private String input;

    @Column(length = 1000)
    private String expectedOutput;

    private boolean isHidden;
    private int points;
}