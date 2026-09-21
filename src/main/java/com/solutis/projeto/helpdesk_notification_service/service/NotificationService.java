package com.solutis.projeto.helpdesk_notification_service.service;

import com.solutis.projeto.helpdesk_notification_service.dto.NotificationResponseDTO;
import com.solutis.projeto.helpdesk_notification_service.entity.Notification;
import com.solutis.projeto.helpdesk_notification_service.repository.NotificationRepository;
import com.solutis.projeto.helpdesk_notification_service.repository.specification.NotificationSpecification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;

    public NotificationService(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    @Transactional
    public void create(Notification notification) {
        notificationRepository.save(notification);
    }

    @Transactional(readOnly = true)
    public Page<NotificationResponseDTO> findAll(Long userId, String enabledFilter, Pageable pageable) {
        Specification<Notification> spec = NotificationSpecification.withFilters(userId, enabledFilter);
        return notificationRepository.findAll(spec, pageable)
                .map(NotificationResponseDTO::fromEntity);
    }

    @Transactional(readOnly = true)
    public Page<NotificationResponseDTO> findAll(Long userId, Pageable pageable) {
        return findAll(userId, "ativados", pageable);
    }

    @Transactional(readOnly = true)
    public NotificationResponseDTO findById(Long id) {
        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Notificação não encontrada com ID: " + id));
        return NotificationResponseDTO.fromEntity(notification);
    }

    @Transactional
    public void markAsRead(Long id) {
        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Notificação não encontrada com ID: " + id));
        notification.setRead(true);
        notificationRepository.save(notification);
    }

    @Transactional
    public void updateTicketEnabled(Long ticketId, boolean ticketEnabled) {
        notificationRepository.updateTicketEnabledByTicketId(ticketId, ticketEnabled);
    }
}
