package com.example.taskManager.model.DTO.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDashboardResponse {
    private UserDashBoard dashboard;
    private long totalMembers;
    private int totalPages;
    private int currentPage;
}
