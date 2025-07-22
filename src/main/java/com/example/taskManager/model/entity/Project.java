package com.example.taskManager.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Entity
@Table(name = "projects")
public class Project {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String description;

    private LocalDate startTime;

    private LocalDate endTime;

    private String status;

    private String type;

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @JsonIgnore
    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL)
    private List<Task> tasks;

}
