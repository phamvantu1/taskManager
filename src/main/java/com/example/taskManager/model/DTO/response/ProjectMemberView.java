package com.example.taskManager.model.DTO.response;

public interface ProjectMemberView {
    String getFullName();
    String getEmail();
    Long getTotalTasks();
    Long getCompletedTasks();
    Long getInProgressTasks();
    Long getPendingTasks();
    Long getOverdueTasks();
}