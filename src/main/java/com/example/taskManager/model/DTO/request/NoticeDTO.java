package com.example.taskManager.model.DTO.request;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
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
