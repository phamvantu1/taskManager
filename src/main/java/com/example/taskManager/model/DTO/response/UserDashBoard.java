package com.example.taskManager.model.DTO.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDashBoard {

    private Set<UserDetailDashBoard> admins;

    private Set<UserDetailDashBoard> leaderDepartments;

    private Set<UserDetailDashBoard> projectManagers;

    private Set<UserDetailDashBoard> members;



}
