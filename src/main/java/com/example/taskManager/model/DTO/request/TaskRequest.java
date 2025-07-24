package com.example.taskManager.model.DTO.request;

import lombok.Data;
import java.time.LocalDate;

@Data
public class TaskRequest {

    private String title;

    private String description;

    private LocalDate startTime;

    private LocalDate endTime;

    private Long assigneeId;

    private Long projectId;

    private Long createdById;

    private Long lever;

    private String status;

}
