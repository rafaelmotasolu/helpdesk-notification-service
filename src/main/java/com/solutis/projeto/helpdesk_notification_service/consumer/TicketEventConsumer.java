package com.solutis.projeto.helpdesk_notification_service.consumer;

import com.solutis.projeto.helpdesk_notification_service.config.RabbitMQConfig;
import com.solutis.projeto.helpdesk_notification_service.entity.Notification;
import com.solutis.projeto.helpdesk_notification_service.event.TicketAssignedEvent;
import com.solutis.projeto.helpdesk_notification_service.event.TicketCreatedEvent;
import com.solutis.projeto.helpdesk_notification_service.event.TicketStatusChangedEvent;
import com.solutis.projeto.helpdesk_notification_service.service.NotificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RabbitListener(queues = RabbitMQConfig.NOTIFICATION_QUEUE)
public class TicketEventConsumer {

    private static final Logger log = LoggerFactory.getLogger(TicketEventConsumer.class);

    private final NotificationService notificationService;

    public TicketEventConsumer(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @RabbitHandler
    public void handleTicketCreated(TicketCreatedEvent event) {
        log.info("Processando TicketCreatedEvent para Ticket #{}", event.ticketId());
        
        Notification notification = new Notification(
            event.ticketId(),
            event.customerId(),
            "Chamado Aberto com Sucesso",
            String.format("Seu chamado '%s' foi registrado com prioridade %s.", event.title(), event.priority()),
            "TICKET_CREATED",
            event.ticketEnabled()
        );
        notificationService.create(notification);
    }

    @RabbitHandler
    public void handleTicketAssigned(TicketAssignedEvent event) {
        log.info("Processando TicketAssignedEvent para Ticket #{}", event.ticketId());

        // 1. Notifica o cliente que um técnico assumiu
        Notification clientNotification = new Notification(
            event.ticketId(),
            event.customerId(),
            "Técnico Atribuído ao seu Chamado",
            String.format("Um técnico foi designado para atender o chamado '%s'.", event.ticketTitle()),
            "TICKET_ASSIGNED"
        );
        notificationService.create(clientNotification);

        // 2. Notifica o técnico que ele recebeu um chamado
        if (event.technicianId() != null) {
            Notification techNotification = new Notification(
                event.ticketId(),
                event.technicianId(),
                "Novo Chamado Atribuído a Você",
                String.format("Você foi designado responsável pelo chamado #%d: '%s'.", event.ticketId(), event.ticketTitle()),
                "TICKET_ASSIGNED_TECH"
            );
            notificationService.create(techNotification);
        }
    }

    @RabbitHandler
    public void handleTicketStatusChanged(TicketStatusChangedEvent event) {
        log.info("Processando TicketStatusChangedEvent para Ticket #{}", event.ticketId());

        Notification clientNotification = new Notification(
            event.ticketId(),
            event.customerId(),
            "Status do Chamado Atualizado",
            String.format("O status do seu chamado '%s' mudou de %s para %s.", 
                event.ticketTitle(), event.oldStatus(), event.newStatus()),
            "TICKET_STATUS_CHANGED"
        );
        notificationService.create(clientNotification);
    }
}
