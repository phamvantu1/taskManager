package com.example.taskManager.model.DTO.response;


import lombok.Data;

import java.time.LocalDateTime;

@Data
public class DepartmentResponse {

    private Long id;

    private String name;

    private String description;

    private String leaderName;

    private String createdByName;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

}
