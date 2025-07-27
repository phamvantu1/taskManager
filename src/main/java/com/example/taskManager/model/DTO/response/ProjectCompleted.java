package com.example.taskManager.model.DTO.response;

import lombok.Data;

import java.time.LocalDate;

@Data
public class ProjectCompleted {

    private Long id;

    private String name;

    private LocalDate completedDate;
}
