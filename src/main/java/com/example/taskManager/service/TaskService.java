package com.example.taskManager.service;

import com.example.taskManager.common.constant.TaskLeverEnum;
import com.example.taskManager.common.constant.TaskStatusEnum;
import com.example.taskManager.common.exception.CustomException;
import com.example.taskManager.common.exception.ResponseCode;
import com.example.taskManager.mapper.TaskMapper;
import com.example.taskManager.model.DTO.request.TaskRequest;
import com.example.taskManager.model.DTO.response.DashboardTaskResponse;
import com.example.taskManager.model.DTO.response.TaskResponse;
import com.example.taskManager.model.entity.Project;
import com.example.taskManager.model.entity.Task;
import com.example.taskManager.model.entity.User;
import com.example.taskManager.repository.ProjectRepository;
import com.example.taskManager.repository.TaskRepository;
import com.example.taskManager.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class TaskService {
    
    
    private final UserRepository userRepository;
    private final TaskRepository taskRepository;
    private final ProjectRepository projectRepository;


    public Map<String, String > createTask(TaskRequest taskRequest) {
        try{
            Task task = new Task();
            
            User assignee = userRepository.findById(taskRequest.getAssigneeId())
                    .orElseThrow(() -> new CustomException(ResponseCode.USER_NOT_FOUND));

            User creator = userRepository.findById(taskRequest.getCreatedById())
                    .orElseThrow(() -> new CustomException(ResponseCode.USER_NOT_FOUND));

            if (taskRequest.getProjectId()!= null){
                Project project = projectRepository.findById(taskRequest.getProjectId())
                        .orElseThrow(() -> new CustomException(ResponseCode.PROJECT_NOT_FOUND));
                task.setProject(project);
            }

            task.setTitle(taskRequest.getTitle());
            task.setDescription(taskRequest.getDescription());
            task.setStatus(TaskStatusEnum.PENDING.name());
            task.setAssignedTo(assignee);
            task.setCreatedBy(taskRequest.getCreatedById());
            task.setStartTime(taskRequest.getStartTime());
            task.setEndTime(taskRequest.getEndTime());
            task.setLever(taskRequest.getLever());
            task.setCreatedAt(LocalDateTime.now());
            task.setUpdatedAt(LocalDateTime.now());
            task.setProcess(0L);
            
            taskRepository.save(task);

            return Map.of("message", "Tạo mới công việc thành công");
        }catch(CustomException e){
            throw e;
        }catch (Exception e) {
            throw new RuntimeException("Có lỗi khi tạo mới công việc " + e.getMessage());
        }
    }

    public Page<TaskResponse> getAllTasks(Integer page, Integer size, String textSearch, String startTime, String endTime, Long projectId, String status) {
        try {

            textSearch = (textSearch != null && textSearch.trim().isEmpty()) ? null : textSearch;
            startTime = (startTime != null && startTime.trim().isEmpty()) ? null : startTime;
            endTime = (endTime != null && endTime.trim().isEmpty()) ? null : endTime;
            status = (status != null && status.trim().isEmpty()) ? null : status;

            if(status != null){
                status = TaskStatusEnum.fromLevel(Integer.parseInt(status)).name();
            }

            Pageable pageable = PageRequest.of(page, size);
            Page<Task> tasks = taskRepository.getAllTasks(textSearch, startTime, endTime, projectId,status,pageable);

            return tasks.map(task -> {
                User createdByUser = userRepository.findById(task.getCreatedBy())
                        .orElse(null);
                return TaskMapper.toTaskResponse(task, createdByUser);
            });
        } catch(CustomException e){
            throw e;
        }catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }


    public DashboardTaskResponse getDashboardTasksByProject(Long projectId) {
        try {

            if(projectId != null){
                Project project = projectRepository.findById(projectId)
                        .orElseThrow(() -> new CustomException(ResponseCode.PROJECT_NOT_FOUND));
            }

            long inProgressCount = 0;
            long completedCount = 0;
            long pendingCount = 0;
            long overdueCount = 0;

            DashboardTaskResponse dashboardResponse = new DashboardTaskResponse();

            // Lấy danh sách các task của dự án
            List<Task> tasks = taskRepository.findAllByProjectId(projectId);
            for (Task task : tasks) {
                String status = task.getStatus();
                if ("PROCESSING".equalsIgnoreCase(status)) {
                    inProgressCount++;
                } else if ("COMPLETED".equalsIgnoreCase(status)) {
                    completedCount++;
                } else if ("PENDING".equalsIgnoreCase(status)) {
                    pendingCount++;
                }else{
                    overdueCount++;
                }
            }

            dashboardResponse.setProcessing(inProgressCount);
            dashboardResponse.setCompleted(completedCount);
            dashboardResponse.setPending(pendingCount);
            dashboardResponse.setOverdue(overdueCount);
            dashboardResponse.setTotal((long) tasks.size());

            return dashboardResponse;


        } catch(CustomException e){
            throw e;
        }catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public TaskResponse getTaskDetails(Long taskId) {
        try {
            var task =  taskRepository.findById(taskId)
                    .orElseThrow(() -> new CustomException(ResponseCode.TASK_NOT_FOUND));

            User createdByUser = userRepository.findById(task.getCreatedBy())
                    .orElseThrow((() -> new CustomException(ResponseCode.USER_NOT_FOUND)));

            return TaskMapper.toTaskResponse(task, createdByUser);
        } catch(CustomException e){
            throw e;
        }catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public Map<String, String> updateTask(Long taskId, TaskRequest taskRequest) {
        try{

            Task task = taskRepository.findById(taskId)
                    .orElseThrow(() -> new CustomException(ResponseCode.TASK_NOT_FOUND));

            if (taskRequest.getAssigneeId() != null){
                User assignee = userRepository.findById(taskRequest.getAssigneeId())
                        .orElseThrow(() -> new CustomException(ResponseCode.USER_NOT_FOUND));
                task.setAssignedTo(assignee);
            }

            task.setTitle(taskRequest.getTitle());
            task.setDescription(taskRequest.getDescription());
            task.setStartTime(taskRequest.getStartTime());
            task.setEndTime(taskRequest.getEndTime());
            task.setStatus(taskRequest.getStatus() != null ? taskRequest.getStatus() : TaskStatusEnum.PENDING.name());
            task.setLever(taskRequest.getLever());
            task.setUpdatedAt(LocalDateTime.now());

            taskRepository.save(task);

            return Map.of("message", "update task successfully");

        } catch(CustomException e){
            throw e;
        }catch (Exception e) {
            throw new RuntimeException("Có lỗi trong quá trình cập nhập "+ e.getMessage());
        }
    }




}
