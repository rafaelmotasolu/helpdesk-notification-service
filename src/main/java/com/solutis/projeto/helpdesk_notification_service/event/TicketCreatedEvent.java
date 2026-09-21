package com.solutis.projeto.helpdesk_notification_service.event;

import java.time.LocalDateTime;

public record TicketCreatedEvent(
    Long ticketId,
    String title,
    String priority,
    String status,
    String category,
    Long customerId,
    boolean ticketEnabled,
    LocalDateTime occurredOn
) {
    public TicketCreatedEvent(Long ticketId, String title, String priority,
                              String status, String category, Long customerId, boolean ticketEnabled) {
        this(ticketId, title, priority, status, category, customerId, ticketEnabled, LocalDateTime.now());
    }

    public TicketCreatedEvent(Long ticketId, String title, String priority,
                              String status, String category, Long customerId) {
        this(ticketId, title, priority, status, category, customerId, true, LocalDateTime.now());
    }
}