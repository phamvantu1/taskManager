package com.example.taskManager.model.DTO.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDetailDashBoard {

    private Long id;

    private String fullName;

    private String role;

}
