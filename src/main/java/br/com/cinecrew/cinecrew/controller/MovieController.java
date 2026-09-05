package br.com.cinecrew.cinecrew.controller;

import br.com.cinecrew.cinecrew.dto.response.MovieSearchResultResponse;
import br.com.cinecrew.cinecrew.dto.response.MovieResponse;
import br.com.cinecrew.cinecrew.service.MovieService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/movies")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Filmes", description = "Busca e importação de filmes via TMDB")
public class MovieController {

    private final MovieService movieService;

    @GetMapping("/search")
    @Operation(summary = "Busca filmes no TMDB por título")
    public ResponseEntity<List<MovieSearchResultResponse>> search(@RequestParam String query, @RequestParam(defaultValue = "1") int page) {
        return ResponseEntity.ok(movieService.searchMovies(query, page));
    }

    @PostMapping("/import/{tmdbId}")
    @Operation(summary = "Importa (ou retorna, se já existir) um filme do TMDB para o banco local")
    public ResponseEntity<MovieResponse> importMovie(@PathVariable Long tmdbId) {
        return ResponseEntity.ok(movieService.importMovie(tmdbId));
    }
}