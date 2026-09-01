package br.com.cinecrew.cinecrew.controller;

import br.com.cinecrew.cinecrew.dto.request.AddToWatchlistRequest;
import br.com.cinecrew.cinecrew.dto.response.WatchlistItemResponse;
import br.com.cinecrew.cinecrew.service.WatchlistService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;

@RestController
@RequestMapping("/watchlist")
@RequiredArgsConstructor
@Slf4j
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Lista de Desejos", description = "Gerenciamento da lista de filmes que o usuário deseja assistir")
public class WatchlistController {

    private final WatchlistService watchlistService;

    @PostMapping
    @Operation(summary = "Adiciona um filme à lista de desejos do usuário autenticado")
    public ResponseEntity<WatchlistItemResponse> addToWatchlist(JwtAuthenticationToken authentication, @Valid @RequestBody AddToWatchlistRequest request) {
        Long userId = extractUserId(authentication.getToken());

        log.info("Usuário id={} adicionando filme id={} à lista de desejos", userId, request.movieId());

        WatchlistItemResponse response = watchlistService.addToWatchlist(userId, request);

        log.info("Filme id={} adicionado à lista de desejos do usuário id={} com sucesso", request.movieId(), userId);

        return ResponseEntity.ok(response);
    }

    @GetMapping
    @Operation(summary = "Lista os filmes da lista de desejos do usuário autenticado")
    public ResponseEntity<Page<WatchlistItemResponse>> getMyWatchlist(JwtAuthenticationToken authentication, @PageableDefault(size = 20) Pageable pageable) {
        Long userId = extractUserId(authentication.getToken());

        log.info("Listando lista de desejos do usuário id={}", userId);

        return ResponseEntity.ok(watchlistService.getMyWatchlist(userId, pageable));
    }

    @DeleteMapping("/{movieId}")
    @Operation(summary = "Remove um filme da lista de desejos do usuário autenticado")
    public ResponseEntity<Void> removeFromWatchlist(JwtAuthenticationToken authentication, @PathVariable Long movieId) {
        Long userId = extractUserId(authentication.getToken());

        log.info("Usuário id={} removendo filme id={} da lista de desejos", userId, movieId);

        watchlistService.removeFromWatchlist(userId, movieId);

        log.info("Filme id={} removido da lista de desejos do usuário id={} com sucesso", movieId, userId);

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{movieId}/exists")
    @Operation(summary = "Verifica se um filme específico já está na lista de desejos")
    public ResponseEntity<Boolean> isInWatchlist(JwtAuthenticationToken authentication, @PathVariable Long movieId) {
        Long userId = extractUserId(authentication.getToken());

        log.debug("Verificando se filme id={} está na lista de desejos do usuário id={}", movieId, userId);

        return ResponseEntity.ok(watchlistService.isInWatchlist(userId, movieId));
    }

    private Long extractUserId(Jwt jwt) {
        return Long.valueOf(Objects.requireNonNull(jwt.getSubject()));
    }
}