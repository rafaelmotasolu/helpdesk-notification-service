package com.solutis.projeto.helpdesk_notification_service.entity;

import com.solutis.projeto.helpdesk_notification_service.dto.NotificationResponseDTO;
import com.solutis.projeto.helpdesk_notification_service.event.TicketCreatedEvent;
import com.solutis.projeto.helpdesk_notification_service.event.TicketStatusChangedEvent;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class NotificationTest {

    @Test
    @DisplayName("Deve inicializar Notification com ticketEnabled true por padrão no construtor de 5 parâmetros")
    void shouldInitializeWithTicketEnabledTrueByDefault() {
        Notification notification = new Notification(
                10L,
                1L,
                "Chamado Criado",
                "Seu chamado foi criado com sucesso",
                "TICKET_CREATED"
        );

        assertTrue(notification.isTicketEnabled(), "O valor padrão de ticketEnabled deve ser true");
        assertEquals(10L, notification.getTicketId());
        assertEquals(1L, notification.getUserId());
        assertFalse(notification.isRead());
    }

    @Test
    @DisplayName("Deve permitir criar Notification com ticketEnabled false explicitamente")
    void shouldCreateNotificationWithTicketEnabledFalse() {
        Notification notification = new Notification(
                10L,
                1L,
                "Chamado Desativado",
                "Seu chamado foi desativado",
                "TICKET_DISABLED",
                false
        );

        assertFalse(notification.isTicketEnabled());
        notification.setTicketEnabled(true);
        assertTrue(notification.isTicketEnabled());
    }

    @Test
    @DisplayName("Deve mapear corretamente ticketEnabled no NotificationResponseDTO")
    void shouldMapTicketEnabledInNotificationResponseDTO() {
        Notification notification = new Notification(
                10L,
                2L,
                "Técnico Atribuído",
                "Um técnico foi atribuído",
                "TICKET_ASSIGNED",
                true
        );

        NotificationResponseDTO dto = NotificationResponseDTO.fromEntity(notification);

        assertNotNull(dto);
        assertEquals(10L, dto.ticketId());
        assertEquals(2L, dto.userId());
        assertTrue(dto.ticketEnabled());

        notification.setTicketEnabled(false);
        NotificationResponseDTO dtoDisabled = NotificationResponseDTO.fromEntity(notification);
        assertFalse(dtoDisabled.ticketEnabled());
    }

    @Test
    @DisplayName("Deve criar TicketCreatedEvent com ticketEnabled")
    void shouldCreateTicketCreatedEventWithTicketEnabled() {
        TicketCreatedEvent eventDefault = new TicketCreatedEvent(
                100L,
                "Falha na impressora",
                "MEDIUM",
                "OPEN",
                "HARDWARE",
                5L
        );
        assertTrue(eventDefault.ticketEnabled());

        TicketCreatedEvent eventExplicit = new TicketCreatedEvent(
                101L,
                "Falha na rede",
                "HIGH",
                "OPEN",
                "NETWORK",
                6L,
                false,
                LocalDateTime.now()
        );
        assertFalse(eventExplicit.ticketEnabled());
    }

    @Test
    @DisplayName("Deve instanciar TicketStatusChangedEvent compilado corretamente")
    void shouldInstantiateTicketStatusChangedEvent() {
        TicketStatusChangedEvent event = new TicketStatusChangedEvent(
                200L,
                "Problema de software",
                "OPEN",
                "IN_PROGRESS",
                5L,
                10L,
                LocalDateTime.now()
        );

        assertNotNull(event);
        assertEquals(200L, event.ticketId());
        assertEquals("OPEN", event.oldStatus());
        assertEquals("IN_PROGRESS", event.newStatus());
    }
}

