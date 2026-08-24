package br.com.cinecrew.cinecrew.dto.response;

import br.com.cinecrew.cinecrew.model.enums.EventStatus;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public record EventResponse(
        Long id,
        String movieTitle,
        String posterUrl,
        OffsetDateTime sessionDateTime,
        String cinemaName,
        BigDecimal totalAmount,
        EventStatus status,
        UserSummaryResponse organizer
) {}