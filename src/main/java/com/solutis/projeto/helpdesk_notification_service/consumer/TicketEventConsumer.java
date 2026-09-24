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
        
        // 1. Notifica o cliente que abriu o chamado
        Notification clientNotification = new Notification(
            event.ticketId(),
            event.customerId(),
            event.customerId(),
            null,
            "Chamado Aberto com Sucesso",
            String.format("Seu chamado '%s' foi registrado com prioridade %s.", event.title(), event.priority()),
            "TICKET_CREATED",
            event.ticketEnabled()
        );
        notificationService.create(clientNotification);

        // 2. Notifica todos os técnicos disponíveis sobre o novo chamado sem técnico
        Notification techNotification = new Notification(
            event.ticketId(),
            null,
            event.customerId(),
            "TECHNICIAN",
            "Novo Chamado Disponível",
            String.format("O chamado #%d ('%s') foi aberto e está aguardando atendimento.", event.ticketId(), event.title()),
            "TICKET_CREATED_UNASSIGNED",
            event.ticketEnabled()
        );
        notificationService.create(techNotification);
    }

    @RabbitHandler
    public void handleTicketAssigned(TicketAssignedEvent event) {
        log.info("Processando TicketAssignedEvent para Ticket #{}", event.ticketId());

        // 1. Notifica o cliente que um técnico assumiu
        Notification clientNotification = new Notification(
            event.ticketId(),
            event.customerId(),
            event.customerId(),
            null,
            "Técnico Atribuído ao seu Chamado",
            String.format("Um técnico foi designado para atender o chamado '%s'.", event.ticketTitle()),
            "TICKET_ASSIGNED",
            true
        );
        notificationService.create(clientNotification);

        // 2. Notifica o técnico que ele recebeu um chamado
        if (event.technicianId() != null) {
            Notification techNotification = new Notification(
                event.ticketId(),
                event.technicianId(),
                event.customerId(),
                null,
                "Novo Chamado Atribuído a Você",
                String.format("Você foi designado responsável pelo chamado #%d: '%s'.", event.ticketId(), event.ticketTitle()),
                "TICKET_ASSIGNED_TECH",
                true
            );
            notificationService.create(techNotification);
        }
    }

    @RabbitHandler
    public void handleTicketStatusChanged(TicketStatusChangedEvent event) {
        log.info("Processando TicketStatusChangedEvent para Ticket #{}", event.ticketId());

        // 1. Notifica o cliente
        Notification clientNotification = new Notification(
            event.ticketId(),
            event.customerId(),
            event.customerId(),
            null,
            "Status do Chamado Atualizado",
            String.format("O status do seu chamado '%s' mudou de %s para %s.", 
                event.ticketTitle(), event.oldStatus(), event.newStatus()),
            "TICKET_STATUS_CHANGED",
            true
        );
        notificationService.create(clientNotification);

        // 2. Notifica o técnico responsável, se atribuído e diferente do cliente
        if (event.technicianId() != null && !event.technicianId().equals(event.customerId())) {
            Notification techNotification = new Notification(
                event.ticketId(),
                event.technicianId(),
                event.customerId(),
                null,
                "Status do Chamado Atualizado",
                String.format("O chamado #%d ('%s') teve o status atualizado para %s.",
                    event.ticketId(), event.ticketTitle(), event.newStatus()),
                "TICKET_STATUS_CHANGED",
                true
            );
            notificationService.create(techNotification);
        }
    }
}
