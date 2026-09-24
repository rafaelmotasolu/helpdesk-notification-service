package com.solutis.projeto.helpdesk_notification_service.consumer;

import com.solutis.projeto.helpdesk_notification_service.entity.Notification;
import com.solutis.projeto.helpdesk_notification_service.event.TicketAssignedEvent;
import com.solutis.projeto.helpdesk_notification_service.event.TicketCreatedEvent;
import com.solutis.projeto.helpdesk_notification_service.event.TicketStatusChangedEvent;
import com.solutis.projeto.helpdesk_notification_service.service.NotificationService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TicketEventConsumerTest {

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private TicketEventConsumer consumer;

    @Test
    @DisplayName("Deve processar TicketCreatedEvent e criar Notificação para cliente e técnicos disponíveis")
    void shouldProcessTicketCreatedEventWithTicketEnabledTrue() {
        TicketCreatedEvent event = new TicketCreatedEvent(
                10L,
                "Sem acesso a VPN",
                "HIGH",
                "OPEN",
                "NETWORK",
                2L,
                true,
                LocalDateTime.now()
        );

        consumer.handleTicketCreated(event);

        ArgumentCaptor<Notification> captor = ArgumentCaptor.forClass(Notification.class);
        verify(notificationService, times(2)).create(captor.capture());

        var list = captor.getAllValues();
        assertEquals(2, list.size());

        // 1. Notificação do cliente
        Notification clientNotif = list.get(0);
        assertEquals(10L, clientNotif.getTicketId());
        assertEquals(2L, clientNotif.getUserId());
        assertTrue(clientNotif.isTicketEnabled());
        assertEquals("TICKET_CREATED", clientNotif.getEventType());

        // 2. Notificação para técnicos (chamado sem técnico)
        Notification techNotif = list.get(1);
        assertEquals(10L, techNotif.getTicketId());
        assertNull(techNotif.getUserId());
        assertEquals("TECHNICIAN", techNotif.getRecipientRole());
        assertEquals(2L, techNotif.getCustomerId());
        assertEquals("TICKET_CREATED_UNASSIGNED", techNotif.getEventType());
    }

    @Test
    @DisplayName("Deve processar TicketAssignedEvent e criar notificações para cliente e técnico")
    void shouldProcessTicketAssignedEventForClientAndTechnician() {
        TicketAssignedEvent event = new TicketAssignedEvent(
                15L,
                "Computador não liga",
                3L,
                8L,
                LocalDateTime.now()
        );

        consumer.handleTicketAssigned(event);

        ArgumentCaptor<Notification> captor = ArgumentCaptor.forClass(Notification.class);
        verify(notificationService, times(2)).create(captor.capture());

        var list = captor.getAllValues();
        assertEquals(2, list.size());

        // Notificação para o cliente
        Notification clientNotif = list.get(0);
        assertEquals(15L, clientNotif.getTicketId());
        assertEquals(3L, clientNotif.getUserId());
        assertTrue(clientNotif.isTicketEnabled());
        assertEquals("TICKET_ASSIGNED", clientNotif.getEventType());

        // Notificação para o técnico
        Notification techNotif = list.get(1);
        assertEquals(15L, techNotif.getTicketId());
        assertEquals(8L, techNotif.getUserId());
        assertTrue(techNotif.isTicketEnabled());
        assertEquals("TICKET_ASSIGNED_TECH", techNotif.getEventType());
    }

    @Test
    @DisplayName("Deve processar TicketStatusChangedEvent e criar notificação para o cliente e técnico")
    void shouldProcessTicketStatusChangedEvent() {
        TicketStatusChangedEvent event = new TicketStatusChangedEvent(
                20L,
                "Falha de software",
                "OPEN",
                "IN_PROGRESS",
                5L,
                9L,
                LocalDateTime.now()
        );

        consumer.handleTicketStatusChanged(event);

        ArgumentCaptor<Notification> captor = ArgumentCaptor.forClass(Notification.class);
        verify(notificationService, times(2)).create(captor.capture());

        var list = captor.getAllValues();
        assertEquals(2, list.size());

        Notification clientNotif = list.get(0);
        assertEquals(20L, clientNotif.getTicketId());
        assertEquals(5L, clientNotif.getUserId());
        assertEquals("TICKET_STATUS_CHANGED", clientNotif.getEventType());

        Notification techNotif = list.get(1);
        assertEquals(20L, techNotif.getTicketId());
        assertEquals(9L, techNotif.getUserId());
        assertEquals("TICKET_STATUS_CHANGED", techNotif.getEventType());
    }
}
