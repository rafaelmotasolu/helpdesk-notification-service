package com.solutis.projeto.helpdesk_notification_service.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.amqp.core.*;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String TICKET_EXCHANGE = "ticket.events";
    public static final String NOTIFICATION_QUEUE = "notification.ticket-events.queue";

    public static final String ROUTING_KEY_CREATED = "ticket.created";
    public static final String ROUTING_KEY_ASSIGNED = "ticket.assigned";
    public static final String ROUTING_KEY_STATUS_CHANGED = "ticket.status-changed";

    @Bean
    public TopicExchange ticketExchange() {
        return new TopicExchange(TICKET_EXCHANGE, true, false);
    }

    @Bean
    public Queue notificationQueue() {
        return QueueBuilder.durable(NOTIFICATION_QUEUE).build();
    }

    @Bean
    public Binding bindingCreated(Queue notificationQueue, TopicExchange ticketExchange) {
        return BindingBuilder.bind(notificationQueue).to(ticketExchange).with(ROUTING_KEY_CREATED);
    }

    @Bean
    public Binding bindingAssigned(Queue notificationQueue, TopicExchange ticketExchange) {
        return BindingBuilder.bind(notificationQueue).to(ticketExchange).with(ROUTING_KEY_ASSIGNED);
    }

    @Bean
    public Binding bindingStatusChanged(Queue notificationQueue, TopicExchange ticketExchange) {
        return BindingBuilder.bind(notificationQueue).to(ticketExchange).with(ROUTING_KEY_STATUS_CHANGED);
    }

    @SuppressWarnings("removal")
    @Bean
    public MessageConverter jsonMessageConverter() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        return new Jackson2JsonMessageConverter(mapper);
    }
}