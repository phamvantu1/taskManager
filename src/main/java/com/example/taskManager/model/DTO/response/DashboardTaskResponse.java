package com.example.taskManager.model.DTO.response;

import lombok.Data;

@Data
public class DashboardTaskResponse {

    private Long IN_PROGRESS;

    private Long COMPLETED;

    private Long PENDING;

    private Long OVERDUE;

}
