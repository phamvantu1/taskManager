package com.example.taskManager.model.DTO.request;

import com.example.taskManager.model.entity.User;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ProjectRequest {

    private String name;

    private String description;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    private Long userId;


}
