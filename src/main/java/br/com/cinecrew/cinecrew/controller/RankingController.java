package br.com.cinecrew.cinecrew.controller;

import br.com.cinecrew.cinecrew.dto.response.RankingEntryResponse;
import br.com.cinecrew.cinecrew.service.RankingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/clubs/{clubId}/ranking")
@RequiredArgsConstructor
@Slf4j
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Ranking", description = "Gamificação e pontuação dos membros do clube")
public class RankingController {

    private final RankingService rankingService;

    @GetMapping
    @Operation(summary = "Lista o ranking completo do clube")
    public ResponseEntity<List<RankingEntryResponse>> getClubRanking(JwtAuthenticationToken authentication, @PathVariable Long clubId) {
        Long userId = extractUserId(authentication.getToken());

        log.info("Usuário id={} consultando ranking do clube id={}", userId, clubId);

        return ResponseEntity.ok(rankingService.getClubRanking(userId, clubId));
    }

    @GetMapping("/me")
    @Operation(summary = "Retorna a posição do usuário autenticado no ranking do clube")
    public ResponseEntity<RankingEntryResponse> getMyRanking(JwtAuthenticationToken authentication, @PathVariable Long clubId) {
        Long userId = extractUserId(authentication.getToken());

        log.info("Usuário id={} consultando própria posição no ranking do clube id={}", userId, clubId);

        return ResponseEntity.ok(rankingService.getMyRanking(userId, clubId));
    }

    private Long extractUserId(Jwt jwt) {
        return Long.valueOf(jwt.getSubject());
    }
}