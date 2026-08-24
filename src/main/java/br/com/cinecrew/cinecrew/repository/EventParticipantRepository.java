package br.com.cinecrew.cinecrew.repository;

import br.com.cinecrew.cinecrew.model.EventParticipant;
import br.com.cinecrew.cinecrew.model.enums.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface EventParticipantRepository extends JpaRepository<EventParticipant, Long> {
    List<EventParticipant> findByEventId(Long eventId);

    Optional<EventParticipant> findByEventIdAndUserId(Long eventId, Long userId);

    List<EventParticipant> findByEventIdAndPaymentStatus(Long eventId, PaymentStatus paymentStatus);

    long countByEventIdAndPaymentStatus(Long eventId, PaymentStatus paymentStatus);
}
