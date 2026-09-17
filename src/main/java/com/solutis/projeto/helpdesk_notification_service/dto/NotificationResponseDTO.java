package com.solutis.projeto.helpdesk_notification_service.dto;

import com.solutis.projeto.helpdesk_notification_service.entity.Notification;
import java.time.LocalDateTime;

public record NotificationResponseDTO(
    Long id,
    Long ticketId,
    Long userId,
    String title,
    String message,
    String eventType,
    boolean read,
    LocalDateTime createdAt
) {
    public static NotificationResponseDTO fromEntity(Notification notification) {
        return new NotificationResponseDTO(
            notification.getId(),
            notification.getTicketId(),
            notification.getUserId(),
            notification.getTitle(),
            notification.getMessage(),
            notification.getEventType(),
            notification.isRead(),
            notification.getCreatedAt()
        );
    }
}
