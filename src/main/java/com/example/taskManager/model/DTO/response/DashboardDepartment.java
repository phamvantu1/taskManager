package com.example.taskManager.model.DTO.response;

import com.example.taskManager.model.entity.Project;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
public class DashboardDepartment {

    private Set<NewParticipient> listNewUsers;

    private Set<ProjectInProgress> listProjectsInProgress;

    private Set<ProjectCompleted> listProjectsCompleted;
}
