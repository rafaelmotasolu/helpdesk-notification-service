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
    @DisplayName("Deve processar TicketCreatedEvent e criar Notificação preservando ticketEnabled true")
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
        verify(notificationService).create(captor.capture());

        Notification saved = captor.getValue();
        assertEquals(10L, saved.getTicketId());
        assertEquals(2L, saved.getUserId());
        assertTrue(saved.isTicketEnabled(), "A notificação deve herdar ticketEnabled true do evento");
        assertEquals("TICKET_CREATED", saved.getEventType());
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
    @DisplayName("Deve processar TicketStatusChangedEvent e criar notificação para o cliente")
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
        verify(notificationService).create(captor.capture());

        Notification saved = captor.getValue();
        assertEquals(20L, saved.getTicketId());
        assertEquals(5L, saved.getUserId());
        assertTrue(saved.isTicketEnabled());
        assertEquals("TICKET_STATUS_CHANGED", saved.getEventType());
    }
}

