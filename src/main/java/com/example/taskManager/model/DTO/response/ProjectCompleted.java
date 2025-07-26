package com.example.taskManager.model.DTO.response;

import lombok.Data;

@Data
public class ProjectCompleted {

    private Long id;

    private String name;

    private String completedDate;
}
