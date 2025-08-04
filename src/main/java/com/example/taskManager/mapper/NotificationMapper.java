package com.example.taskManager.mapper;

import com.example.taskManager.model.DTO.request.NoticeDTO;
import com.example.taskManager.model.entity.Notification;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface NotificationMapper {

    NoticeDTO toDTO(Notification notification);

    Notification toEntity(NoticeDTO dto);
}