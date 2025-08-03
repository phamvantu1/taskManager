package com.example.taskManager.service;


import com.example.taskManager.common.exception.CustomException;
import com.example.taskManager.model.DTO.response.*;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import com.example.taskManager.repository.ProjectRepository;
import com.example.taskManager.repository.TaskRepository;
import com.example.taskManager.repository.UserRepository;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

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

    public Page<UserTaskDashBoardResponse> getDashboardUserTask(Long departmentId, String textSearch, String startTime, String endTime, int page, int size) {
        try{

            Pageable pageable = PageRequest.of(page, size);

            if (!StringUtils.hasText(startTime)) {
                startTime = null;
            }
            if (!StringUtils.hasText(endTime)) {
                endTime = null;
            }

            if( !StringUtils.hasText(textSearch)) {
                textSearch = null;
            }

            var resultPage = taskRepository.getDashboardUserTasks(departmentId, textSearch, startTime, endTime, pageable);
            List<UserTaskDashBoardResponse> responses = resultPage.stream().map(row -> {
                Object[] r = (Object[]) row;
                UserTaskDashBoardResponse dto = new UserTaskDashBoardResponse();
                dto.setName((String) r[0]);
                dto.setDepartmentName((String) r[1]);
                dto.setProcessing(((Number) r[2]).longValue());
                dto.setOverdue(((Number) r[3]).longValue());
                dto.setWaitCompleted(((Number) r[4]).longValue());
                dto.setCompleted(((Number) r[5]).longValue());
                dto.setPending(((Number) r[6]).longValue());
                dto.setTotalTasks(((Number) r[7]).longValue());
                dto.setPlusPoint(((Number) r[8]).longValue());
                dto.setMinusPoint(((Number) r[9]).longValue());
                dto.setTotalPoint(((Number) r[10]).longValue());
                return dto;
            }).toList();


            return new PageImpl<>(responses, pageable, resultPage.getTotalElements());

        }catch (CustomException e){
            throw e;
        }catch (Exception e){
            throw new RuntimeException(e.getMessage());
        }
    }

    public void exportDashboardUserTask(Long departmentId, String textSearch, String startTime, String endTime, HttpServletResponse httpServletResponse) {
        try {

            if (!StringUtils.hasText(startTime)) {
                startTime = null;
            }
            if (!StringUtils.hasText(endTime)) {
                endTime = null;
            }

            if (!StringUtils.hasText(textSearch)) {
                textSearch = null;
            }

            var response =  taskRepository.exportDashboardUserTasks(departmentId, textSearch, startTime, endTime);

            exportToExcel(httpServletResponse, response);


        } catch (CustomException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public void exportToExcel(HttpServletResponse response, List<UserTaskDashBoard> data) throws Exception {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("User Task Dashboard");

        // Header
        Row header = sheet.createRow(0);
        String[] headers = {
                "Tên nhân viên", "Phòng ban", "Đang thực hiện", "Quá hạn", "Chờ duyệt hoàn thành",
                "Hoàn thành", "Chờ thực hiện ", "Tổng số công việc", "Điểm trừ", "Điểm cộng", "Tổng điểm"
        };
        for (int i = 0; i < headers.length; i++) {
            header.createCell(i).setCellValue(headers[i]);
        }

        // Data
        int rowNum = 1;
        for (UserTaskDashBoard dto : data) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(dto.getName());
            row.createCell(1).setCellValue(dto.getDepartmentName());
            row.createCell(2).setCellValue(dto.getProcessing() != null ? dto.getProcessing() : 0);
            row.createCell(3).setCellValue(dto.getOverdue() != null ? dto.getOverdue() : 0);
            row.createCell(4).setCellValue(dto.getWaitCompleted() != null ? dto.getWaitCompleted() : 0);
            row.createCell(5).setCellValue(dto.getCompleted() != null ? dto.getCompleted() : 0);
            row.createCell(6).setCellValue(dto.getPending() != null ? dto.getPending() : 0);
            row.createCell(7).setCellValue(dto.getTotalTasks() != null ? dto.getTotalTasks() : 0);
            row.createCell(8).setCellValue(dto.getPlusPoint() != null ? dto.getPlusPoint() : 0);
            row.createCell(9).setCellValue(dto.getMinusPoint() != null ? dto.getMinusPoint() : 0);
            row.createCell(10).setCellValue(dto.getTotalScore() != null ? dto.getTotalScore() : 0);
        }

        // Set content type and download
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=Bao_Cao_Tong_Quan.xlsx");

        workbook.write(response.getOutputStream());
        workbook.close();
    }

}
