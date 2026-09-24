package com.solutis.projeto.helpdesk_notification_service.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(
    name = "tb_notifications",
    indexes = {
        @Index(name = "idx_notification_user_id", columnList = "user_id"),
        @Index(name = "idx_notification_customer_id", columnList = "customer_id"),
        @Index(name = "idx_notification_recipient_role", columnList = "recipient_role"),
        @Index(name = "idx_notification_ticket_id", columnList = "ticket_id"),
        @Index(name = "idx_notification_created_at", columnList = "created_at"),
        @Index(name = "idx_notification_ticket_enabled", columnList = "ticket_enabled")
    }
)
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "ticket_id", nullable = false)
    private Long ticketId;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "customer_id")
    private Long customerId;

    @Column(name = "recipient_role", length = 30)
    private String recipientRole;

    @Column(nullable = false, length = 150)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String message;

    @Column(name = "event_type", nullable = false, length = 50)
    private String eventType;

    @Column(nullable = false)
    private boolean read = false;

    @Column(name = "ticket_enabled", nullable = false)
    private boolean ticketEnabled = true;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public Notification() {}

    public Notification(Long ticketId, Long userId, String title, String message, String eventType) {
        this(ticketId, userId, null, null, title, message, eventType, true);
    }

    public Notification(Long ticketId, Long userId, String title, String message, String eventType, boolean ticketEnabled) {
        this(ticketId, userId, null, null, title, message, eventType, ticketEnabled);
    }

    public Notification(Long ticketId, Long userId, Long customerId, String recipientRole, String title, String message, String eventType, boolean ticketEnabled) {
        this.ticketId = ticketId;
        this.userId = userId;
        this.customerId = customerId;
        this.recipientRole = recipientRole;
        this.title = title;
        this.message = message;
        this.eventType = eventType;
        this.read = false;
        this.ticketEnabled = ticketEnabled;
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getTicketId() { return ticketId; }
    public void setTicketId(Long ticketId) { this.ticketId = ticketId; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public Long getCustomerId() { return customerId; }
    public void setCustomerId(Long customerId) { this.customerId = customerId; }

    public String getRecipientRole() { return recipientRole; }
    public void setRecipientRole(String recipientRole) { this.recipientRole = recipientRole; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public String getEventType() { return eventType; }
    public void setEventType(String eventType) { this.eventType = eventType; }

    public boolean isRead() { return read; }
    public void setRead(boolean read) { this.read = read; }

    public boolean isTicketEnabled() { return ticketEnabled; }
    public void setTicketEnabled(boolean ticketEnabled) { this.ticketEnabled = ticketEnabled; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}