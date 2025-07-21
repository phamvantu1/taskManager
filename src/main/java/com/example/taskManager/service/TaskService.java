package com.example.taskManager.service;

import com.example.taskManager.common.constant.TaskEnum;
import com.example.taskManager.common.exception.CustomException;
import com.example.taskManager.common.exception.ResponseCode;
import com.example.taskManager.model.DTO.request.TaskRequest;
import com.example.taskManager.model.entity.Project;
import com.example.taskManager.model.entity.Task;
import com.example.taskManager.model.entity.User;
import com.example.taskManager.repository.ProjectRepository;
import com.example.taskManager.repository.TaskRepository;
import com.example.taskManager.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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

            Project project = projectRepository.findById(taskRequest.getProjectId())
                    .orElseThrow(() -> new CustomException(ResponseCode.PROJECT_NOT_FOUND));

            task.setTitle(taskRequest.getTitle());
            task.setDescription(taskRequest.getDescription());
            task.setStatus(TaskEnum.PENDING.name());
            task.setAssignedTo(assignee);
            task.setCreatedBy(taskRequest.getCreatedById());
            task.setProject(project);
            task.setStartTime(taskRequest.getStartTime());
            task.setEndTime(taskRequest.getEndTime());
            task.setLever(taskRequest.getLever());
            
            taskRepository.save(task);
            
            
            return Map.of("message", "create task successfully");
        }catch(CustomException e){
            throw e;
        }catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }


}
