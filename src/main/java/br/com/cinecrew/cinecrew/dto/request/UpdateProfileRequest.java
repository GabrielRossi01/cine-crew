package br.com.cinecrew.cinecrew.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record UpdateProfileRequest(
        @NotBlank(message = "Nome é obrigatório")
        @Size(max = 120, message = "Nome deve ter no máximo 120 caracteres")
        String name,

        @NotBlank(message = "Username é obrigatório")
        @Size(min = 3, max = 30, message = "Username deve ter entre 3 e 30 caracteres")
        @Pattern(
                regexp = "^[a-zA-Z0-9._-]+$",
                message = "Username deve conter apenas letras, números, ponto, hífen ou underscore"
        )
        String username
) {}