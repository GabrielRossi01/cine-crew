package br.com.cinecrew.cinecrew.dto.request;

import br.com.cinecrew.cinecrew.model.enums.EventStatus;
import jakarta.validation.constraints.NotNull;

public record UpdateEventStatusRequest(
        @NotNull(message = "O status do evento é obrigatório")
        EventStatus status
) {}
