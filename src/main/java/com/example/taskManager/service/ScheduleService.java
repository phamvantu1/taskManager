package com.example.taskManager.service;


import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ScheduleService {
    private final JwtBlacklistService jwtBlacklistService;
    private final TaskService taskService;

    @Scheduled(cron = "0 0 0 * * *")
    public void automaticJob(){
        taskService.checkExpiredTasks();
        jwtBlacklistService.cleanExpiredTokens();
    }
}


