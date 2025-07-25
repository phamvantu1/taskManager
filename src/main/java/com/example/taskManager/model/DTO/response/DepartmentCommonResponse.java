package com.example.taskManager.model.DTO.response;


import lombok.Data;

@Data
public class DepartmentCommonResponse {

    private Long id;

    private String name;

    private String description;

    private String leaderName;

    private String createdByName;

    private String createdAt;

    private String updatedAt;

    private Long numberOfUsers;

    private Long numberOfProjects;

}
