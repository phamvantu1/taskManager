package com.example.taskManager.model.DTO.request;

import jakarta.persistence.Column;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class NoticeDTO {

    private Long id;

    private Long userId;

    private String title;

    private String message;

    private Boolean isRead ;

    private LocalDateTime createdAt;

    private String referenceType;

    private Long referenceId;

}
