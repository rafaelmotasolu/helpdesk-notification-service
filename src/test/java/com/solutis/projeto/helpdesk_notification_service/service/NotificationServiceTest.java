package com.solutis.projeto.helpdesk_notification_service.service;

import com.solutis.projeto.helpdesk_notification_service.dto.NotificationResponseDTO;
import com.solutis.projeto.helpdesk_notification_service.entity.Notification;
import com.solutis.projeto.helpdesk_notification_service.repository.NotificationRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {

    @Mock
    private NotificationRepository notificationRepository;

    @InjectMocks
    private NotificationService notificationService;

    @Test
    @DisplayName("Deve salvar notificação ao chamar create")
    void shouldSaveNotificationOnCreate() {
        Notification notification = new Notification(1L, 2L, "Título", "Mensagem", "TICKET_CREATED", true);

        notificationService.create(notification);

        verify(notificationRepository).save(notification);
    }

    @Test
    @DisplayName("Deve listar notificações filtrando com enabledFilter ativados por padrão")
    void shouldListNotificationsWithEnabledFilterAtivadosByDefault() {
        Pageable pageable = PageRequest.of(0, 10);
        Notification notification = new Notification(1L, 2L, "Título", "Mensagem", "TICKET_CREATED", true);
        Page<Notification> page = new PageImpl<>(List.of(notification));

        when(notificationRepository.findAll(ArgumentMatchers.<Specification<Notification>>any(), eq(pageable))).thenReturn(page);

        Page<NotificationResponseDTO> result = notificationService.findAll(2L, "ativados", pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertTrue(result.getContent().get(0).ticketEnabled());
        verify(notificationRepository).findAll(ArgumentMatchers.<Specification<Notification>>any(), eq(pageable));
    }

    @Test
    @DisplayName("Deve listar notificações desativadas quando enabledFilter for desativados")
    void shouldListDisabledNotificationsWhenFilterIsDesativados() {
        Pageable pageable = PageRequest.of(0, 10);
        Notification disabledNotification = new Notification(1L, 2L, "Título", "Mensagem", "TICKET_CREATED", false);
        Page<Notification> page = new PageImpl<>(List.of(disabledNotification));

        when(notificationRepository.findAll(ArgumentMatchers.<Specification<Notification>>any(), eq(pageable))).thenReturn(page);

        Page<NotificationResponseDTO> result = notificationService.findAll(null, "desativados", pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertFalse(result.getContent().get(0).ticketEnabled());
        verify(notificationRepository).findAll(ArgumentMatchers.<Specification<Notification>>any(), eq(pageable));
    }

    @Test
    @DisplayName("Deve marcar notificação como lida")
    void shouldMarkNotificationAsRead() {
        Notification notification = new Notification(1L, 2L, "Título", "Mensagem", "TICKET_CREATED");
        notification.setId(10L);
        assertFalse(notification.isRead());

        when(notificationRepository.findById(10L)).thenReturn(Optional.of(notification));

        notificationService.markAsRead(10L);

        assertTrue(notification.isRead());
        verify(notificationRepository).save(notification);
    }

    @Test
    @DisplayName("Deve atualizar ticketEnabled por ticketId")
    void shouldUpdateTicketEnabledByTicketId() {
        notificationService.updateTicketEnabled(5L, false);

        verify(notificationRepository).updateTicketEnabledByTicketId(5L, false);
    }
}

