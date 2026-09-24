package com.solutis.projeto.helpdesk_notification_service.controller;

import com.solutis.projeto.helpdesk_notification_service.dto.NotificationResponseDTO;
import com.solutis.projeto.helpdesk_notification_service.security.SecurityUtils;
import com.solutis.projeto.helpdesk_notification_service.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/notifications")
@Tag(name = "Notificações", description = "Consulta de notificações geradas por eventos de chamados")
@SecurityRequirement(name = "bearerAuth")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @GetMapping
    @Operation(summary = "Listar notificações com paginação e filtros opcionais por userId e status do ticket (ativados, desativados, todos)")
    public ResponseEntity<Page<NotificationResponseDTO>> findAll(
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false, defaultValue = "ativados") String enabledFilter,
            @PageableDefault(page = 0, size = 15, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        Long currentUserId = SecurityUtils.getCurrentUserId();
        String currentRole = SecurityUtils.getCurrentRole();

        if (currentUserId != null) {
            return ResponseEntity.ok(notificationService.findAllForUser(currentUserId, currentRole, userId, enabledFilter, pageable));
        }

        return ResponseEntity.ok(notificationService.findAll(userId, enabledFilter, pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Consultar notificação por ID")
    public ResponseEntity<NotificationResponseDTO> findById(@PathVariable Long id) {
        NotificationResponseDTO dto = notificationService.findById(id);
        Long currentUserId = SecurityUtils.getCurrentUserId();
        String currentRole = SecurityUtils.getCurrentRole();

        if ("CLIENT".equalsIgnoreCase(currentRole) && currentUserId != null && !currentUserId.equals(dto.userId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Você não tem permissão para visualizar esta notificação.");
        }

        return ResponseEntity.ok(dto);
    }

    @PatchMapping("/{id}/read")
    @Operation(summary = "Marcar notificação como lida")
    public ResponseEntity<Void> markAsRead(@PathVariable Long id) {
        notificationService.markAsRead(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/read-all")
    @Operation(summary = "Marcar todas as notificações do usuário autenticado como lidas")
    public ResponseEntity<Void> markAllAsRead() {
        Long currentUserId = SecurityUtils.getCurrentUserId();
        String currentRole = SecurityUtils.getCurrentRole();
        notificationService.markAllAsReadForUser(currentUserId, currentRole);
        return ResponseEntity.noContent().build();
    }
}