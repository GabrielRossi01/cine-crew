package br.com.cinecrew.cinecrew.controller;

import br.com.cinecrew.cinecrew.dto.request.CreateClubRequest;
import br.com.cinecrew.cinecrew.dto.response.ClubMemberResponse;
import br.com.cinecrew.cinecrew.dto.response.ClubResponse;
import br.com.cinecrew.cinecrew.dto.response.InviteResponse;
import br.com.cinecrew.cinecrew.service.ClubService;
import br.com.cinecrew.cinecrew.service.InviteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/clubs")
@RequiredArgsConstructor
@Slf4j
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Clubes", description = "Criação, listagem e convites de clubes")
public class ClubController {

    private final ClubService clubService;
    private final InviteService inviteService;

    @PostMapping
    @Operation(summary = "Cria um novo clube; o criador se torna ADMIN automaticamente")
    public ResponseEntity<ClubResponse> createClub(JwtAuthenticationToken authentication, @Valid @RequestBody CreateClubRequest request) {
        Long userId = extractUserId(authentication.getToken());

        log.info("Usuário id={} criando clube com nome='{}'", userId, request.name());

        ClubResponse response = clubService.createClub(userId, request);

        log.info("Clube id={} criado com sucesso pelo usuário id={}", response.id(), userId);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    @Operation(summary = "Lista os clubes dos quais o usuário autenticado é membro")
    public ResponseEntity<List<ClubResponse>> getMyClubs(JwtAuthenticationToken authentication) {
        Long userId = extractUserId(authentication.getToken());

        log.info("Listando clubes do usuário id={}", userId);

        return ResponseEntity.ok(clubService.getMyClubs(userId));
    }

    @GetMapping("/{clubId}")
    @Operation(summary = "Retorna os detalhes de um clube")
    public ResponseEntity<ClubResponse> getClubById(JwtAuthenticationToken authentication, @PathVariable Long clubId) {
        Long userId = extractUserId(authentication.getToken());

        log.info("Usuário id={} consultando clube id={}", userId, clubId);

        return ResponseEntity.ok(clubService.getClubById(userId, clubId));
    }

    @GetMapping("/{clubId}/members")
    @Operation(summary = "Lista os membros de um clube")
    public ResponseEntity<List<ClubMemberResponse>> getMembers(JwtAuthenticationToken authentication, @PathVariable Long clubId) {
        Long userId = extractUserId(authentication.getToken());

        log.info("Usuário id={} listando membros do clube id={}", userId, clubId);

        return ResponseEntity.ok(clubService.getMembers(userId, clubId));
    }

    @DeleteMapping("/{clubId}/members/{memberUserId}")
    @Operation(summary = "Remove um membro do clube (somente ADMIN)")
    public ResponseEntity<Void> removeMember(JwtAuthenticationToken authentication, @PathVariable Long clubId, @PathVariable Long memberUserId) {
        Long userId = extractUserId(authentication.getToken());

        log.info("Usuário id={} removendo membro id={} do clube id={}", userId, memberUserId, clubId);

        clubService.removeMember(userId, clubId, memberUserId);

        log.info("Membro id={} removido do clube id={} com sucesso", memberUserId, clubId);

        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{clubId}/invite")
    @Operation(summary = "Gera um link de convite temporário (somente ADMIN)")
    public ResponseEntity<InviteResponse> createInvite(JwtAuthenticationToken authentication, @PathVariable Long clubId) {
        Long userId = extractUserId(authentication.getToken());

        log.info("Usuário id={} gerando convite para o clube id={}", userId, clubId);

        InviteResponse response = inviteService.createInvite(userId, clubId);

        log.info("Convite gerado para o clube id={} com expiração em {}", clubId, response.expiresAt());

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/join/{inviteCode}")
    @Operation(summary = "Ingressa em um clube usando um código de convite")
    public ResponseEntity<Void> joinClub(JwtAuthenticationToken authentication, @PathVariable String inviteCode) {
        Long userId = extractUserId(authentication.getToken());

        log.info("Usuário id={} tentando ingressar via convite", userId);

        inviteService.joinClub(userId, inviteCode);

        log.info("Usuário id={} ingressou no clube com sucesso via convite", userId);

        return ResponseEntity.ok().build();
    }

    private Long extractUserId(Jwt jwt) {
        return Long.valueOf(jwt.getSubject());
    }
}