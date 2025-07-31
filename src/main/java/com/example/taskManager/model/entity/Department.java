package com.example.taskManager.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Set;


@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "departments")
public class Department {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String description;

    @OneToMany(mappedBy = "department", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private Set<DepartmentUser> departmentUsers;

    @OneToMany
    @JsonIgnore
    private Set<Project> project;

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "created_by")
    private User createdBy;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private String status; // ACTIVE, INACTIVE, ARCHIVED


}
