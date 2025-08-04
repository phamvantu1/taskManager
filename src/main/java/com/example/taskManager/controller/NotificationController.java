package com.example.taskManager.controller;


import com.example.taskManager.common.exception.Response;
import com.example.taskManager.model.DTO.request.NoticeDTO;
import com.example.taskManager.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @PostMapping("/create")
    public ResponseEntity<Response<?>> createNotification(@RequestBody NoticeDTO noticeDTO) {
        notificationService.createNotification(noticeDTO);
        return ResponseEntity.ok(Response.success("Notification created successfully"));
    }

    @GetMapping("/get-all-notifications")
    public ResponseEntity<Response<?>> getAllNotifications(@RequestParam(name = "userId") Long userId,
                                                           @RequestParam(name = "page", defaultValue = "0") int page,
                                                           @RequestParam(name = "size", defaultValue = "10") int size) {
        return ResponseEntity.ok(Response.success(notificationService.getAllNotifications(userId, page, size)));
    }

    @PutMapping("/mark-as-read/{notificationId}")
    public ResponseEntity<Response<?>> markNotificationAsRead(@PathVariable Long notificationId) {
        notificationService.markNotificationAsRead(notificationId);
        return ResponseEntity.ok(Response.success("Notification marked as read successfully"));
    }

}
