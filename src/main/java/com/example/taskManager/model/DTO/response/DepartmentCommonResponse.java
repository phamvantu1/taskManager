package com.example.taskManager.model.DTO.response;


import lombok.Data;

import java.time.LocalDateTime;

@Data
public class DepartmentCommonResponse {

    private Long id;

    private String name;

    private String description;

    private String leaderName;

    private Long leaderId;

    private String createdByName;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private Long numberOfUsers;

    private Long numberOfProjects;

}
