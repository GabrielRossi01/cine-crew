package br.com.cinecrew.cinecrew.dto.response;

import br.com.cinecrew.cinecrew.model.enums.PaymentStatus;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public record EventBalanceResponse(
        Long eventId,
        BigDecimal totalAmount,
        BigDecimal totalPaid,
        BigDecimal totalPending,
        List<ParticipantBalance> participants
) {
    public record ParticipantBalance(
            Long userId,
            String name,
            BigDecimal amountOwed,
            PaymentStatus paymentStatus,
            Instant paidAt
    ) {
    }
}