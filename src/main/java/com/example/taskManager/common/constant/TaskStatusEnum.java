package com.example.taskManager.common.constant;

public enum TaskStatusEnum {
    PENDING(0),
    PROCESSING(1),
    COMPLETED(2),
    OVERDUE(3),
    WAIT_COMPLETED(4);

    private final int level;
    TaskStatusEnum(int level) {
        this.level = level;
    }
    public int getLevel() {
        return level;
    }

    public static TaskStatusEnum fromLevel(int level) {
        for (TaskStatusEnum status : TaskStatusEnum.values()) {
            if (status.getLevel() == level) {
                return status;
            }
        }
        throw new IllegalArgumentException("Invalid task status level: " + level);
    }
}
