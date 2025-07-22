package com.example.taskManager.common.constant;

public enum TaskLeverEnum {
    EASY(1),
    MEDIUM(2),
    HARD(3);

    private final int level;

    TaskLeverEnum(int level) {
        this.level = level;
    }
    public int getLevel() {
        return level;
    }

}
