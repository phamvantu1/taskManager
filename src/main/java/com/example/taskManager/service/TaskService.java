package com.example.taskManager.service;

import com.example.taskManager.common.constant.StatusExtend;
import com.example.taskManager.common.constant.TaskLeverEnum;
import com.example.taskManager.common.constant.TaskStatusEnum;
import com.example.taskManager.common.exception.CustomException;
import com.example.taskManager.common.exception.ResponseCode;
import com.example.taskManager.mapper.TaskMapper;
import com.example.taskManager.model.DTO.request.NoticeDTO;
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
import org.hibernate.annotations.ColumnTransformers;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class TaskService {


    private final UserRepository userRepository;
    private final TaskRepository taskRepository;
    private final ProjectRepository projectRepository;
    private final NotificationService notificationService;


    public Map<String, String> createTask(TaskRequest taskRequest) {
        try {
            Task task = new Task();

            User assignee = userRepository.findById(taskRequest.getAssigneeId())
                    .orElseThrow(() -> new CustomException(ResponseCode.USER_NOT_FOUND));

            User creator = userRepository.findById(taskRequest.getCreatedById())
                    .orElseThrow(() -> new CustomException(ResponseCode.USER_NOT_FOUND));

            if (taskRequest.getProjectId() != null) {
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
            task.setIsExtend(false);
            task.setIsDeleted(false);

            var newTask = taskRepository.save(task);

            NoticeDTO noticeDTO = NoticeDTO.builder()
                    .userId(assignee.getId())
                    .title("Công việc mới được giao")
                    .message("Bạn đã được giao công việc: " + task.getTitle())
                    .isRead(false)
                    .createdAt(LocalDateTime.now())
                    .referenceType("TASK")
                    .referenceId(newTask.getId())
                    .build();

            notificationService.createNotification(noticeDTO);

            return Map.of("message", "Tạo mới công việc thành công");
        } catch (CustomException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Có lỗi khi tạo mới công việc " + e.getMessage());
        }
    }

    public Page<TaskResponse> getAllTasks(Integer page, Integer size, String textSearch, String startTime, String endTime, Long projectId, String status, Long type) {
        try {

            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String email = authentication.getName();
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new CustomException(ResponseCode.USER_NOT_FOUND));

            textSearch = (textSearch != null && textSearch.trim().isEmpty()) ? null : textSearch;
            startTime = (startTime != null && startTime.trim().isEmpty()) ? null : startTime;
            endTime = (endTime != null && endTime.trim().isEmpty()) ? null : endTime;
            status = (status != null && status.trim().isEmpty()) ? null : status;

            if (status != null) {
                status = TaskStatusEnum.fromLevel(Integer.parseInt(status)).name();
            }

            Pageable pageable = PageRequest.of(page, size);
            Page<Task> tasks = taskRepository.getAllTasks(textSearch, startTime, endTime, projectId, status, type, user.getId(), pageable);

            return tasks.map(task -> {
                User createdByUser = userRepository.findById(task.getCreatedBy())
                        .orElse(null);
                return TaskMapper.toTaskResponse(task, createdByUser);
            });
        } catch (CustomException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }


    public DashboardTaskResponse getDashboardTasksByProject(Long projectId, Long type) {
        try {
            // type = 0 duoc giao
            // type = 1 giao

            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String email = authentication.getName();
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new CustomException(ResponseCode.USER_NOT_FOUND));

            if (projectId != null) {
                Project project = projectRepository.findById(projectId)
                        .orElseThrow(() -> new CustomException(ResponseCode.PROJECT_NOT_FOUND));
            }

            long inProgressCount = 0;
            long completedCount = 0;
            long pendingCount = 0;
            long overdueCount = 0;
            long waitCompletedCount = 0;

            DashboardTaskResponse dashboardResponse = new DashboardTaskResponse();

            // Lấy danh sách các task của dự án
            List<Task> tasks = taskRepository.findAllByProjectId(projectId, type, user.getId());
            for (Task task : tasks) {
                String status = task.getStatus();
                if ("PROCESSING".equalsIgnoreCase(status)) {
                    inProgressCount++;
                } else if ("COMPLETED".equalsIgnoreCase(status)) {
                    completedCount++;
                } else if ("PENDING".equalsIgnoreCase(status)) {
                    pendingCount++;
                } else if ("WAIT_COMPLETED".equalsIgnoreCase(status)) {
                    waitCompletedCount++;
                } else {
                    overdueCount++;
                }
            }

            dashboardResponse.setProcessing(inProgressCount);
            dashboardResponse.setCompleted(completedCount);
            dashboardResponse.setPending(pendingCount);
            dashboardResponse.setOverdue(overdueCount);
            dashboardResponse.setTotal((long) tasks.size());
            dashboardResponse.setWaitCompleted(waitCompletedCount);

            return dashboardResponse;


        } catch (CustomException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public TaskResponse getTaskDetails(Long taskId) {
        try {
            var task = taskRepository.findById(taskId)
                    .orElseThrow(() -> new CustomException(ResponseCode.TASK_NOT_FOUND));

            User createdByUser = userRepository.findById(task.getCreatedBy())
                    .orElseThrow((() -> new CustomException(ResponseCode.USER_NOT_FOUND)));

            return TaskMapper.toTaskResponse(task, createdByUser);
        } catch (CustomException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Transactional
    public Map<String, String> updateTask(Long taskId, TaskRequest taskRequest) {
        try {

            Task task = taskRepository.findById(taskId)
                    .orElseThrow(() -> new CustomException(ResponseCode.TASK_NOT_FOUND));

            if (taskRequest.getAssigneeId() != null) {
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

        } catch (CustomException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Có lỗi trong quá trình cập nhập " + e.getMessage());
        }
    }

    public Map<String, String> deleteTask(Long taskId) {
        try {

            Task task = taskRepository.findById(taskId).orElseThrow(() -> new CustomException(ResponseCode.TASK_NOT_FOUND));
            task.setIsDeleted(true);
            task.setUpdatedAt(LocalDateTime.now());

            taskRepository.save(task);

            return Map.of("message", "Xóa công việc thành công");
        } catch (CustomException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Có lỗi trong quá trình xóa công việc " + e.getMessage());
        }
    }


    public Map<String, String> markFinishTask(Long taskId, Authentication authentication) {
        try {

            User user = userRepository.findByEmail(authentication.getName())
                    .orElseThrow(() -> new CustomException(ResponseCode.USER_NOT_FOUND));

            Task task = taskRepository.findById(taskId)
                    .orElseThrow(() -> new CustomException(ResponseCode.TASK_NOT_FOUND));

            if (!task.getAssignedTo().getId().equals(user.getId())) {
                throw new CustomException(ResponseCode.YOU_DONT_PERMISSIT_TASK);
            }
            task.setStatus(TaskStatusEnum.WAIT_COMPLETED.name());
            task.setUpdatedAt(LocalDateTime.now());

            taskRepository.save(task);

            NoticeDTO noticeDTO = NoticeDTO.builder()
                    .userId(task.getCreatedBy())
                    .title("Công việc yêu cầu phê duyệt hoàn thành")
                    .message("Công việc yêu cầu phê duyệt hoàn thành: " + task.getTitle())
                    .isRead(false)
                    .createdAt(LocalDateTime.now())
                    .referenceType("TASK")
                    .referenceId(task.getId())
                    .build();

            notificationService.createNotification(noticeDTO);

            return Map.of("message", "Công vệc hoàn thành");

        } catch (CustomException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Có lỗi trong quá trình hoàn thành công việc " + e.getMessage());
        }
    }

    public Map<String, String> approveCompletedTask(Long taskId, Authentication authentication) {
        try {

            User user = userRepository.findByEmail(authentication.getName())
                    .orElseThrow(() -> new CustomException(ResponseCode.USER_NOT_FOUND));

            Task task = taskRepository.findById(taskId)
                    .orElseThrow(() -> new CustomException(ResponseCode.TASK_NOT_FOUND));

            if (!task.getCreatedBy().equals(user.getId())) {
                throw new CustomException(ResponseCode.YOU_DONT_PERMISSIT_TASK);
            }

            task.setStatus(TaskStatusEnum.COMPLETED.name());
            task.setProcess(100L);
            task.setUpdatedAt(LocalDateTime.now());

            taskRepository.save(task);

            NoticeDTO noticeDTO = NoticeDTO.builder()
                    .userId(task.getAssignedTo().getId())
                    .title("Công việc đã được phê duyệt hoàn thành")
                    .message("Công việc đã được phê duyệt hoàn thành : " + task.getTitle())
                    .isRead(false)
                    .createdAt(LocalDateTime.now())
                    .referenceType("TASK")
                    .referenceId(task.getId())
                    .build();

            notificationService.createNotification(noticeDTO);

            return Map.of("message", "Công việc đã được phê duyệt hoàn thành");

        } catch (CustomException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Có lỗi trong quá trình phê duyệt công việc " + e.getMessage());
        }
    }

    public Map<String, String> rejectCompletedTask(Long taskId, Authentication authentication) {
        try {

            User user = userRepository.findByEmail(authentication.getName())
                    .orElseThrow(() -> new CustomException(ResponseCode.USER_NOT_FOUND));

            Task task = taskRepository.findById(taskId)
                    .orElseThrow(() -> new CustomException(ResponseCode.TASK_NOT_FOUND));

            if (!task.getCreatedBy().equals(user.getId())) {
                throw new CustomException(ResponseCode.YOU_DONT_PERMISSIT_TASK);
            }

            task.setStatus(TaskStatusEnum.PROCESSING.name());
            task.setUpdatedAt(LocalDateTime.now());

            taskRepository.save(task);

            NoticeDTO noticeDTO = NoticeDTO.builder()
                    .userId(task.getAssignedTo().getId())
                    .title("Công việc yêu cầu phê duyệt hoàn thành đã bị từ chối")
                    .message("Công việc yêu cầu phê duyệt hoàn thành đã bị từ chối" + task.getTitle())
                    .isRead(false)
                    .createdAt(LocalDateTime.now())
                    .referenceType("TASK")
                    .referenceId(task.getId())
                    .build();

            notificationService.createNotification(noticeDTO);

            return Map.of("message", "Công việc đã bị từ chối hoàn thành");

        } catch (CustomException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Có lỗi trong quá trình từ chối công việc " + e.getMessage());
        }
    }

    public Map<String, String> extendTask(Long taskId, String newEndTime) {
        try {

            Task task = taskRepository.findById(taskId)
                    .orElseThrow(() -> new CustomException(ResponseCode.TASK_NOT_FOUND));

            task.setEndTime(LocalDate.parse(newEndTime));
            task.setIsExtend(true);
            task.setUpdatedAt(LocalDateTime.now());

            taskRepository.save(task);

            return Map.of("message", "Gia hạn công việc thành công");

        } catch (CustomException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Có lỗi trong quá trình gia hạn công việc " + e.getMessage());
        }
    }




}
