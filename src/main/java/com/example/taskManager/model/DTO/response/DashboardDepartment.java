package com.example.taskManager.model.DTO.response;

import com.example.taskManager.model.entity.Project;
import com.example.taskManager.model.entity.User;
import lombok.Data;

import java.util.Set;

@Data
public class DashboardDepartment {

    private Set<User> listNewUsers;

    private Set<Project> listProjectsInProgress;

    private Set<Project> listProjectsCompleted;
}
