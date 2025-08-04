package com.example.taskManager.model.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "notifications")
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;

    private String title;

    @Column(columnDefinition = "TEXT")
    private String message;

    private Boolean isRead ;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    private String referenceType; // e.g., "task", "project", etc.

    private Long referenceId;

}
