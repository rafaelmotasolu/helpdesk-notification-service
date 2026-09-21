package com.solutis.projeto.helpdesk_notification_service.repository.specification;

import com.solutis.projeto.helpdesk_notification_service.entity.Notification;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.jpa.domain.Specification;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationSpecificationTest {

    @Mock
    private Root<Notification> root;

    @Mock
    private CriteriaQuery<?> query;

    @Mock
    private CriteriaBuilder cb;

    @Mock
    private Predicate conjunctionPredicate;

    @Mock
    private Predicate conditionPredicate;

    @Mock
    private Path<Boolean> booleanPath;

    @Mock
    private Path<Long> longPath;

    @BeforeEach
    void setUp() {
        lenient().when(cb.conjunction()).thenReturn(conjunctionPredicate);
        lenient().when(cb.and(any(), any())).thenReturn(conjunctionPredicate);
    }

    @Test
    @DisplayName("Deve filtrar ativados por padrão quando enabledFilter for nulo")
    void shouldFilterAtivadosByDefaultWhenNull() {
        doReturn(booleanPath).when(root).get("ticketEnabled");
        when(cb.isTrue(booleanPath)).thenReturn(conditionPredicate);

        Specification<Notification> spec = NotificationSpecification.withFilters(null, null);
        Predicate predicate = spec.toPredicate(root, query, cb);

        assertNotNull(predicate);
        verify(cb).isTrue(booleanPath);
    }

    @Test
    @DisplayName("Deve filtrar desativados quando enabledFilter for desativados")
    void shouldFilterDesativados() {
        doReturn(booleanPath).when(root).get("ticketEnabled");
        when(cb.isFalse(booleanPath)).thenReturn(conditionPredicate);

        Specification<Notification> spec = NotificationSpecification.withFilters(null, "desativados");
        Predicate predicate = spec.toPredicate(root, query, cb);

        assertNotNull(predicate);
        verify(cb).isFalse(booleanPath);
    }

    @Test
    @DisplayName("Não deve filtrar por ticketEnabled quando enabledFilter for todos")
    void shouldNotFilterTicketEnabledWhenTodos() {
        Specification<Notification> spec = NotificationSpecification.withFilters(null, "todos");
        Predicate predicate = spec.toPredicate(root, query, cb);

        assertNotNull(predicate);
        verify(cb, never()).isTrue(any());
        verify(cb, never()).isFalse(any());
    }

    @Test
    @DisplayName("Deve adicionar filtro de userId quando informado")
    void shouldAddUserIdFilter() {
        doReturn(longPath).when(root).get("userId");
        doReturn(booleanPath).when(root).get("ticketEnabled");
        when(cb.equal(longPath, 10L)).thenReturn(conditionPredicate);
        when(cb.isTrue(booleanPath)).thenReturn(conditionPredicate);

        Specification<Notification> spec = NotificationSpecification.withFilters(10L, "ativados");
        Predicate predicate = spec.toPredicate(root, query, cb);

        assertNotNull(predicate);
        verify(cb).equal(longPath, 10L);
        verify(cb).isTrue(booleanPath);
    }
}
