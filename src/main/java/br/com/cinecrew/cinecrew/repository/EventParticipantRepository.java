package br.com.cinecrew.cinecrew.repository;

import br.com.cinecrew.cinecrew.model.EventParticipant;
import br.com.cinecrew.cinecrew.model.enums.PaymentStatus;
import jakarta.persistence.LockModeType;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface EventParticipantRepository extends JpaRepository<EventParticipant, Long> {
    List<EventParticipant> findByEventId(Long eventId);

    Optional<EventParticipant> findByEventIdAndUserId(Long eventId, Long userId);

    List<EventParticipant> findByEventIdAndPaymentStatus(Long eventId, PaymentStatus paymentStatus);

    long countByEventIdAndPaymentStatus(Long eventId, PaymentStatus paymentStatus);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
            SELECT participant
            FROM EventParticipant participant
            WHERE participant.event.id = :eventId
            AND participant.user.id = :userId
            """)
    Optional<EventParticipant> findByEventIdAndUserIdForUpdate(
            @Param("eventId") Long eventId,
            @Param("userId") Long userId
    );

    boolean existsByEventIdAndUserId(Long eventId, Long userId);
}
