package com.example.taskManager.model.DTO.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DashBoardTaskOverView {

    private Long averageProgress;

    private Long  excellentCount;

    private Long needSupportCount;
}
