package com.example.taskManager.model.entity;


import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "scores")
@Data
public class Score {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "task_id", nullable = false)
    private Long taskId;

    @Column(name = "score_type")
    private String scoreType; // "plus" or "minus"

    @Column(name = "score_value", nullable = false)
    private Long scoreValue; // Positive for plus, negative for minus

    @Column(name = "created_at", nullable = false)
    private String createdAt; // Timestamp of when the score was created
}
