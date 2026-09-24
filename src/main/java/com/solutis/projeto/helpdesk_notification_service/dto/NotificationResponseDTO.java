package com.solutis.projeto.helpdesk_notification_service.dto;

import com.solutis.projeto.helpdesk_notification_service.entity.Notification;
import java.time.LocalDateTime;

public record NotificationResponseDTO(
    Long id,
    Long ticketId,
    Long userId,
    Long customerId,
    String recipientRole,
    String title,
    String message,
    String eventType,
    boolean read,
    boolean ticketEnabled,
    LocalDateTime createdAt
) {
    public static NotificationResponseDTO fromEntity(Notification notification) {
        return new NotificationResponseDTO(
            notification.getId(),
            notification.getTicketId(),
            notification.getUserId(),
            notification.getCustomerId(),
            notification.getRecipientRole(),
            notification.getTitle(),
            notification.getMessage(),
            notification.getEventType(),
            notification.isRead(),
            notification.isTicketEnabled(),
            notification.getCreatedAt()
        );
    }
}
