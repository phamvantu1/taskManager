package com.example.taskManager.model.DTO.request;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class TaskRequest {

    private String title;

    private String description;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    private Long assigneeId;

    private Long projectId;

    private Long createdById;

    private Long lever;

}
