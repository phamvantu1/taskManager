package com.example.taskManager.model.DTO.request;

import lombok.Data;
import java.time.LocalDate;

@Data
public class ProjectRequest {

    private String name;

    private String description;

    private LocalDate startTime;

    private LocalDate endTime;

    private Long ownerId;

    private String type;

    private Long departmentId;


}
