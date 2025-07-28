package com.example.taskManager.model.DTO.response;


import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DashBoardTask {

    private Long completed;

    private Long inProgress;

    private Long pending;

    private Long overdue;


}
