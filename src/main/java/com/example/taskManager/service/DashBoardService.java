package com.example.taskManager.service;


import com.example.taskManager.common.exception.CustomException;
import com.example.taskManager.model.DTO.response.*;
import com.example.taskManager.model.entity.User;
import com.example.taskManager.repository.ProjectRepository;
import com.example.taskManager.repository.TaskRepository;
import com.example.taskManager.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class DashBoardService {


    private final UserRepository userRepository;
    private final ProjectRepository projectRepository;
    private final TaskRepository taskRepository;

    public List<DashBoardOverView> getDashboardOverview(Long departmentId){
        try{

            Long totalAllNumber = userRepository.totalAllNumber(departmentId);

            Long totalNewUsers = userRepository.totalNewUsers(departmentId);

            Long totalAllProjects = projectRepository.totalAllProjects(departmentId);

            Long totalProjectFinished = projectRepository.totalProjectFinished(departmentId);

            Long totalAllTasks = taskRepository.totalAllTasks(departmentId);

            Long totalTaskCompleted = taskRepository.totalTaskCompleted(departmentId);


            List<DashBoardOverView> result = List.of(
                    DashBoardOverView.builder()
                            .title("THỐNG KÊ NHÂN SỰ")
                            .value(totalAllNumber)
                            .subtitle(totalNewUsers + " Nhân sự mới")
                            .build(),
                    DashBoardOverView.builder()
                            .title("TỔNG DỰ ÁN")
                            .value(totalAllProjects)
                            .subtitle(totalProjectFinished + " Dự án hoàn thành")
                            .build(),
                    DashBoardOverView.builder()
                            .title("TỔNG SỐ CÔNG VIỆC")
                            .value(totalAllTasks)
                            .subtitle(totalTaskCompleted + " Công việc hoàn thành")
                            .build()
            );

            return result;

        }catch (CustomException e){
            throw e;
        }catch (Exception e){
            throw new RuntimeException(e.getMessage());
        }
    }

    public DashBoardProject getDashboardProjects(Long departmentId, String startTime, String endTime) {
        try{

            if (!StringUtils.hasText(startTime)) {
                startTime = null;
            }
            if (!StringUtils.hasText(endTime)) {
                endTime = null;
            }

            Object result = projectRepository.getProjectDashboardData(departmentId, startTime, endTime);
            Object[] row = (Object[]) result;

            Long completed = row[0] != null ? ((Number) row[0]).longValue() : 0L;
            Long inProgress = row[1] != null ? ((Number) row[1]).longValue() : 0L;
            Long pending = row[2] != null ? ((Number) row[2]).longValue() : 0L;
            Long overdue = row[3] != null ? ((Number) row[3]).longValue() : 0L;

            return  DashBoardProject.builder()
                    .completed(completed)
                    .inProgress(inProgress)
                    .pending(pending)
                    .overdue(overdue)
                    .build();

        }catch (CustomException e){
            throw e;
        }catch (Exception e){
            throw new RuntimeException(e.getMessage());
        }
    }

    public DashBoardTask getDashboardTasks(Long departmentId,Long projectId, String startTime, String endTime) {
        try{

            if (!StringUtils.hasText(startTime)) {
                startTime = null;
            }
            if (!StringUtils.hasText(endTime)) {
                endTime = null;
            }

            log.info("Fetching task dashboard data for departmentId: {}, projectId: {}, startTime: {}, endTime: {}",
                    departmentId, projectId, startTime, endTime);

            Object result = taskRepository.getTaskDashboardData(departmentId, projectId, startTime, endTime);
            Object[] row = (Object[]) result;

            Long completed = row[0] != null ? ((Number) row[0]).longValue() : 0L;
            Long inProgress = row[1] != null ? ((Number) row[1]).longValue() : 0L;
            Long pending = row[2] != null ? ((Number) row[2]).longValue() : 0L;
            Long overdue = row[3] != null ? ((Number) row[3]).longValue() : 0L;

            return  DashBoardTask.builder()
                    .completed(completed)
                    .inProgress(inProgress)
                    .pending(pending)
                    .overdue(overdue)
                    .build();

        }catch (CustomException e){
            throw e;
        }catch (Exception e){
            throw new RuntimeException(e.getMessage());
        }
    }

    public Page<DashBoardUser> getDashboardUsers(Long departmentId, String startTime, String endTime, int page, int size) {
        try{

            Pageable pageable = PageRequest.of(page, size);
            if (!StringUtils.hasText(startTime)) {
                startTime = null;
            }
            if (!StringUtils.hasText(endTime)) {
                endTime = null;
            }

            Page<Object[]> users = userRepository.getDashboardUsers(departmentId, startTime, endTime, pageable);

            return users.map(row -> new DashBoardUser(
                    (String) row[0],
                    row[1] != null ? Math.round(((Number) row[1]).doubleValue() * 100) : 0L  // Convert to %
            ));

        }catch (CustomException e){
            throw e;
        }catch (Exception e){
            throw new RuntimeException(e.getMessage());
        }
    }

    public DashBoardTaskOverView  getDashboardTaskOverview(Long departmentId, String startTime, String endTime) {
        try{

            if (!StringUtils.hasText(startTime)) {
                startTime = null;
            }
            if (!StringUtils.hasText(endTime)) {
                endTime = null;
            }

            List<Object[]> users = userRepository.getDashboardUsersOverView(departmentId, startTime, endTime);

            double totalProgress = 0.0;
            int count = 0;
            int excellentCount = 0;
            int needSupportCount = 0;

            for (Object[] row : users) {
                Double progress = (row[1] != null) ? ((Number) row[1]).doubleValue() : 0.0;

                totalProgress += progress;
                count++;

                if (progress >= 0.9) {
                    excellentCount++;
                } else if (progress < 0.5) {
                    needSupportCount++;
                }
            }

            long averageProgress = count > 0 ? Math.round(totalProgress / count * 100) : 0;

            return DashBoardTaskOverView.builder()
                    .averageProgress(averageProgress) // Tính theo phần trăm
                    .excellentCount((long) excellentCount)
                    .needSupportCount((long) needSupportCount)
                    .build();

        }catch (CustomException e){
            throw e;
        }catch (Exception e){
            throw new RuntimeException(e.getMessage());
        }
    }

}
