package com.example.taskManager.model.DTO.response;

import com.example.taskManager.model.entity.Project;
import com.example.taskManager.model.entity.User;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class TaskResponse {

    private Long id;

    private String title;

    private String description;

    private String status;

    private LocalDate startTime;

    private LocalDate endTime;

    private String nameAssignedTo;

    private String nameCreatedBy;

    private Long lever;

    private Long process;

    private String createdAt;

}
