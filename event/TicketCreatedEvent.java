// TicketCreatedEvent.java
package com.solutis.projeto.helpdesk_notification_service.event;

import java.time.LocalDateTime;

public record TicketCreatedEvent(
    Long ticketId,
    String title,
    String priority,
    String status,
    String category,
    Long customerId,
    LocalDateTime occurredOn
) {}
