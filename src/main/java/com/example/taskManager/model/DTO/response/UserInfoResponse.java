package com.example.taskManager.model.DTO.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserInfoResponse {

    private Long id;

    private String firstName;

    private String lastName;

    private String email;

    private String role;

    private String phone;

    private String gender;

    private String dateOfBirth;

    private String departmentName;

}
