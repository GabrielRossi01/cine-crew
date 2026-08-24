package br.com.cinecrew.cinecrew.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateClubRequest(
        @NotBlank(message = "Nome do clube é obrigatório")
        @Size(max = 100)
        String name,

        @Size(max = 500)
        String description
) {}