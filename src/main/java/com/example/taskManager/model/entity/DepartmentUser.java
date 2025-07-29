package com.example.taskManager.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "department_users")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DepartmentUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "department_id")
    @JsonIgnore
    private Department department;

    @ManyToOne
    @JoinColumn(name = "user_id")
    @JsonIgnore
    private User user;

    private String role;

    private LocalDateTime joinedAt;

}