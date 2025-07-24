package com.example.taskManager.common.constant;

public enum TaskLeverEnum {
    EASY(0),
    MEDIUM(1),
    HARD(2);

    private final int level;

    TaskLeverEnum(int level) {
        this.level = level;
    }
    public int getLevel() {
        return level;
    }

    public static TaskLeverEnum fromLevel(int level) {
        for (TaskLeverEnum lever : TaskLeverEnum.values()) {
            if (lever.getLevel() == level) {
                return lever;
            }
        }
        throw new IllegalArgumentException("Invalid task lever level: " + level);
    }

}
