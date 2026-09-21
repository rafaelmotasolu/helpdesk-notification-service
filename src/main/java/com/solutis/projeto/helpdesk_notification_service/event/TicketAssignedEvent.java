package com.solutis.projeto.helpdesk_notification_service.event;

import java.time.LocalDateTime;

public record TicketAssignedEvent(
    Long ticketId,
    String ticketTitle,
    Long customerId,
    Long technicianId,
    LocalDateTime occurredOn
) {}