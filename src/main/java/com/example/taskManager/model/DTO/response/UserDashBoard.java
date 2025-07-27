package com.example.taskManager.model.DTO.response;

import lombok.Data;

import java.util.Set;

@Data
public class UserDashBoard {

    private Set<UserDetailDashBoard> admins;

    private Set<UserDetailDashBoard> leaderDepartments;

    private Set<UserDetailDashBoard> projectManagers;

    private Set<UserDetailDashBoard> members;



}
