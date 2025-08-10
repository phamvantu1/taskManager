package com.example.taskManager.service;

import com.example.taskManager.common.exception.CustomException;
import com.example.taskManager.common.exception.ResponseCode;
import com.example.taskManager.mapper.NotificationMapper;
import com.example.taskManager.model.DTO.request.NoticeDTO;
import com.example.taskManager.model.entity.Notification;
import com.example.taskManager.model.entity.User;
import com.example.taskManager.repository.NotificationRepository;
import com.example.taskManager.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationMapper notificationMapper;

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

    public void createNotification(NoticeDTO noticeDTO) {
        try {
            Notification notification = new Notification();
            User user = userRepository.findById(noticeDTO.getUserId())
                    .orElseThrow(() -> new CustomException(ResponseCode.USER_NOT_FOUND));

            notification.setUserId(user.getId());
            notification.setTitle(noticeDTO.getTitle());
            notification.setMessage(noticeDTO.getMessage());
            notification.setIsRead(false);
            notification.setCreatedAt(LocalDateTime.now());
            notification.setReferenceId(noticeDTO.getReferenceId());
            notification.setReferenceType(noticeDTO.getReferenceType());

            notificationRepository.save(notification);

        } catch (CustomException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Failed to create notification: " + e.getMessage(), e);
        }
    }

    public Page<NoticeDTO> getAllNotifications(Authentication authentication, int page, int size) {
        try {

            Pageable pageable = PageRequest.of(page, size);


            User user = userRepository.findByEmail(authentication.getName())
                    .orElseThrow(() -> new CustomException(ResponseCode.USER_NOT_FOUND));

            Page<Notification> notifications = notificationRepository.getAllNotifications(user.getId(), pageable);
            return notifications.map(notificationMapper::toDTO);
        } catch (CustomException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Failed to retrieve notifications: " + e.getMessage(), e);
        }
    }

    public Map<String, String> markNotificationAsRead(Long notificationId) {
        try {
            Notification notification = notificationRepository.findById(notificationId)
                    .orElseThrow(() -> new CustomException(ResponseCode.NOTIFICATION_NOT_FOUND));

            notification.setIsRead(true);
            notificationRepository.save(notification);

            return Map.of("message", "Notification marked as read successfully");
        } catch (CustomException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Failed to mark notification as read: " + e.getMessage(), e);
        }
    }
}
