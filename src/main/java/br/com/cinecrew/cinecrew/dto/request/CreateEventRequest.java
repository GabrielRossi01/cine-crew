package br.com.cinecrew.cinecrew.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

public record CreateEventRequest(
        @NotBlank(message = "Título do filme é obrigatório")
        String movieTitle,

        Long tmdbId,

        String posterUrl,

        @NotNull(message = "Data e horário da sessão são obrigatórios")
        OffsetDateTime sessionDateTime,

        @NotBlank(message = "Nome do cinema é obrigatório")
        String cinemaName,

        @NotNull(message = "Valor total é obrigatório")
        @DecimalMin(value = "0.01", message = "Valor total deve ser positivo")
        BigDecimal totalAmount,

        @NotNull(message = "É necessário informar ao menos os IDs dos participantes")
        List<Long> participantUserIds
) {}