package com.example.taskManager.model.DTO.response;


import lombok.Data;

@Data
public class UserTaskDashBoardResponse {

    private String name;

    private String departmentName;

    private Long processing;

    private Long overdue;

    private Long waitCompleted;

    private Long completed;

    private Long pending;

    private Long totalTasks;

    private Long plusPoint;

    private Long minusPoint;

    private Long totalPoint;

}
