package com.smarthirex.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "jobs")
public class Job {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    @Column(length = 1000)
    private String description;

    private String location;

    private String status;
}