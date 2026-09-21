package com.solutis.projeto.helpdesk_notification_service.repository.specification;

import com.solutis.projeto.helpdesk_notification_service.entity.Notification;
import org.springframework.data.jpa.domain.Specification;

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
}

