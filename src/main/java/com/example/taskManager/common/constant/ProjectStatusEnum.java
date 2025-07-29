package com.example.taskManager.common.constant;

public enum ProjectStatusEnum {
    PENDING(0),
    PROCESSING(1),
    COMPLETED(2),
    OVERDUE(3),
    CANCELED(4);

    private final int level;
    ProjectStatusEnum(int level) {
        this.level = level;
    }
    public int getLevel() {
        return level;
    }

    public static ProjectStatusEnum fromLevel(int level) {
        for (ProjectStatusEnum status : ProjectStatusEnum.values()) {
            if (status.getLevel() == level) {
                return status;
            }
        }
        throw new IllegalArgumentException("Invalid task status level: " + level);
    }

}

