package com.example.taskManager.model.DTO.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class DashboardTaskResponse {

    @JsonProperty("IN_PROGRESS")
    private Long inProgress;

    @JsonProperty("COMPLETED")
    private Long completed;

    @JsonProperty("PENDING")
    private Long pending;

    @JsonProperty("OVERDUE")
    private Long overdue;

    @JsonProperty("TOTAL")
    private Long total;

}
