package com.example.taskManager.model.DTO.response;

public interface UserTaskDashBoard {

    String getName();
    String getDepartmentName();
    Long getProcessing();
    Long getOverdue();
    Long getWaitCompleted();
    Long getCompleted();
    Long getPending();
    Long getTotalTasks();
    Long getPlusPoint();
    Long getMinusPoint();
    Long getTotalScore();

}
