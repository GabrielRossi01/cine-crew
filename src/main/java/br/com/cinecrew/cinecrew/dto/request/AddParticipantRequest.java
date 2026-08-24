package br.com.cinecrew.cinecrew.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record AddParticipantRequest(
        @NotNull(message = "Usuário é obrigatório")
        Long userId,

        @NotNull(message = "Valor devido é obrigatório")
        @DecimalMin(value = "0.00", message = "Valor devido não pode ser negativo")
        BigDecimal amountOwed
) {}