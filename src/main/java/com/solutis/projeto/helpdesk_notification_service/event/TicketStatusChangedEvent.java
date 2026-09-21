package com.solutis.projeto.helpdesk_notification_service.event;

import java.time.LocalDateTime;

public record TicketStatusChangedEvent(
    Long ticketId,
    String ticketTitle,
    String oldStatus,
    String newStatus,
    Long customerId,
    Long technicianId,
    LocalDateTime occurredOn
) {}