package com.example.taskManager.repository;

import com.example.taskManager.model.entity.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface NotificationRepository extends JpaRepository<Notification, Long> {


    @Query(value = "SELECT * from notifications n " +
            "where n.user_id = :userId " +
            "order by n.created_at desc "
            , nativeQuery = true)
    Page<Notification> getAllNotifications(@Param("userId") Long userId,
                                           Pageable pageable);
}
