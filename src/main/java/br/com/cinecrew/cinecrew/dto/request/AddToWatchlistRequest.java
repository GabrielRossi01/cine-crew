package br.com.cinecrew.cinecrew.dto.request;

import jakarta.validation.constraints.NotNull;

public record AddToWatchlistRequest(

        @NotNull(message = "movieId é obrigatório")
        Long movieId
) {}
