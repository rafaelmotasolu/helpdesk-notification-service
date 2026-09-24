package com.solutis.projeto.helpdesk_notification_service.repository;

import com.solutis.projeto.helpdesk_notification_service.entity.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long>, JpaSpecificationExecutor<Notification> {
    Page<Notification> findByUserId(Long userId, Pageable pageable);
    List<Notification> findByTicketId(Long ticketId);

    @Modifying
    @Query("UPDATE Notification n SET n.ticketEnabled = :ticketEnabled WHERE n.ticketId = :ticketId")
    void updateTicketEnabledByTicketId(@Param("ticketId") Long ticketId, @Param("ticketEnabled") boolean ticketEnabled);

    @Modifying
    @Query("UPDATE Notification n SET n.read = true WHERE n.userId = :userId AND n.read = false")
    void markAllAsReadByUserId(@Param("userId") Long userId);
}