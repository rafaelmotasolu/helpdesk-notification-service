package com.solutis.projeto.helpdesk_notification_service.repository.specification;

import com.solutis.projeto.helpdesk_notification_service.entity.Notification;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class NotificationSpecification {

    public static Specification<Notification> withFilters(Long userId, String enabledFilter) {
        return (root, query, criteriaBuilder) -> {
            var predicate = criteriaBuilder.conjunction();

            if (userId != null) {
                predicate = criteriaBuilder.and(predicate, criteriaBuilder.equal(root.get("userId"), userId));
            }

            // Filtro por ticketEnabled: "ativados" (padrão), "desativados" ou "todos"
            if (enabledFilter != null && !enabledFilter.isBlank()) {
                String normalized = enabledFilter.trim().toLowerCase();
                if (normalized.equals("desativados") || normalized.equals("false") || normalized.equals("disabled")) {
                    predicate = criteriaBuilder.and(predicate, criteriaBuilder.isFalse(root.get("ticketEnabled")));
                } else if (normalized.equals("todos") || normalized.equals("all")) {
                    // Retorna todos (sem filtro de ticketEnabled)
                } else {
                    // Padrão "ativados"
                    predicate = criteriaBuilder.and(predicate, criteriaBuilder.isTrue(root.get("ticketEnabled")));
                }
            } else {
                // Padrão quando não informado: "ativados"
                predicate = criteriaBuilder.and(predicate, criteriaBuilder.isTrue(root.get("ticketEnabled")));
            }

            return predicate;
        };
    }

    public static Specification<Notification> withUserAndFilters(
            Long currentUserId,
            String currentRole,
            Long filterUserId,
            String enabledFilter
    ) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            // 1. Filtragem por audiência/perfil
            if ("CLIENT".equalsIgnoreCase(currentRole)) {
                // Clientes só veem notificações direcionadas ao seu próprio userId
                predicates.add(cb.equal(root.get("userId"), currentUserId));
            } else if ("TECHNICIAN".equalsIgnoreCase(currentRole)) {
                // Técnicos veem notificações próprias OU notificações de chamados sem técnico (TECHNICIAN),
                // exceto se forem os próprios clientes criadores do chamado
                Predicate isDirectRecipient = cb.equal(root.get("userId"), currentUserId);

                Predicate isTechBroadcast = cb.and(
                        cb.equal(root.get("recipientRole"), "TECHNICIAN"),
                        cb.or(
                                cb.isNull(root.get("customerId")),
                                cb.notEqual(root.get("customerId"), currentUserId)
                        )
                );

                predicates.add(cb.or(isDirectRecipient, isTechBroadcast));
            } else if ("ADMIN".equalsIgnoreCase(currentRole)) {
                // Admin pode ver todas ou filtrar por filterUserId se fornecido
                if (filterUserId != null) {
                    predicates.add(cb.equal(root.get("userId"), filterUserId));
                }
            } else {
                // Fallback quando não há role
                if (filterUserId != null) {
                    predicates.add(cb.equal(root.get("userId"), filterUserId));
                } else if (currentUserId != null) {
                    predicates.add(cb.equal(root.get("userId"), currentUserId));
                }
            }

            // 2. Filtro por ticketEnabled
            if (enabledFilter != null && !enabledFilter.isBlank()) {
                String normalized = enabledFilter.trim().toLowerCase();
                if (normalized.equals("desativados") || normalized.equals("false") || normalized.equals("disabled")) {
                    predicates.add(cb.isFalse(root.get("ticketEnabled")));
                } else if (!normalized.equals("todos") && !normalized.equals("all")) {
                    predicates.add(cb.isTrue(root.get("ticketEnabled")));
                }
            } else {
                predicates.add(cb.isTrue(root.get("ticketEnabled")));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
