package br.com.cinecrew.cinecrew.controller;

import br.com.cinecrew.cinecrew.dto.response.MovieSearchResultResponse;
import br.com.cinecrew.cinecrew.dto.response.MovieResponse;
import br.com.cinecrew.cinecrew.service.MovieService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/movies")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Validated
@Tag(name = "Filmes", description = "Busca e importação de filmes via TMDB")
public class MovieController {

    private final MovieService movieService;

    @GetMapping("/search")
    public ResponseEntity<List<MovieSearchResultResponse>> search(
            @RequestParam
            @NotBlank(message = "Informe um título para buscar")
            String query,

            @RequestParam(defaultValue = "1")
            @Min(value = 1, message = "A página deve ser maior que zero")
            int page
    ) {
        return ResponseEntity.ok(
                movieService.searchMovies(query.trim(), page)
        );
    }

    @PostMapping("/import/{tmdbId}")
    @Operation(summary = "Importa (ou retorna, se já existir) um filme do TMDB para o banco local")
    public ResponseEntity<MovieResponse> importMovie(@PathVariable Long tmdbId) {
        return ResponseEntity.ok(movieService.importMovie(tmdbId));
    }
}