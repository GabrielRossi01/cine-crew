package br.com.cinecrew.cinecrew.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreatePostRequest(
        @NotBlank(message = "URL da imagem é obrigatória")
        String imageUrl,

        @Size(max = 1000)
        String caption
) {}