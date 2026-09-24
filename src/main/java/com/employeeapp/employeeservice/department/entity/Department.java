package com.employeeapp.employeeservice.department.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;

@Getter
@Setter
@Entity
@Table(name = "department")
public class Department {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String name;

    private String description;

    @Column(name = "\"createdAt\"")
    private OffsetDateTime createdAt;

    @Column(name = "\"updatedAt\"")
    private OffsetDateTime updatedAt;
}