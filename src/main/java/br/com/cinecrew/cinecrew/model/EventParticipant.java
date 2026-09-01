package br.com.cinecrew.cinecrew.model;

import br.com.cinecrew.cinecrew.model.enums.PaymentStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(
        name = "event_participants",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_event_participants_event_user",
                        columnNames = {"event_id", "user_id"}
                )
        }
)
public class EventParticipant extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "event_participants_seq")
    @SequenceGenerator(name = "event_participants_seq", sequenceName = "event_participants_seq", allocationSize = 50)
    @Column(nullable = false, updatable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "amount_owed", nullable = false, precision = 10, scale = 2)
    private BigDecimal amountOwed;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    @Column(name = "payment_status", nullable = false, length = 20)
    private PaymentStatus paymentStatus = PaymentStatus.PENDING;

    @Column(name = "paid_at")
    private Instant paidAt;
}