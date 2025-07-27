package com.example.taskManager.model.entity;

import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DepartmentUserId implements Serializable {

    private Long departmentId;

    private Long userId;

}