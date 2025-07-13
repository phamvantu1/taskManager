package com.example.taskManager.model.DTO.request;

import lombok.Data;

@Data
public class ChangePasswordByOtpRequest {

    private String email;

    private String newPassword;

    private String confirmNewPassword;
}
