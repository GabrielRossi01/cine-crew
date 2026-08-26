package br.com.cinecrew.cinecrew.controller;

import br.com.cinecrew.cinecrew.dto.request.CreateEventRequest;
import br.com.cinecrew.cinecrew.dto.request.UpdateEventStatusRequest;
import br.com.cinecrew.cinecrew.dto.response.EventResponse;
import br.com.cinecrew.cinecrew.service.EventService;
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
@RequiredArgsConstructor
@Slf4j
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Ingressos", description = "Criação e consulta de rolês ao cinema")
public class EventController {

    private final EventService eventService;

    @PostMapping("/clubs/{clubId}/events")
    @Operation(summary = "Cria um novo evento/rolê dentro de um clube")
    public ResponseEntity<EventResponse> createEvent(JwtAuthenticationToken authentication, @PathVariable Long clubId, @Valid @RequestBody CreateEventRequest request) {
        Long userId = extractUserId(authentication.getToken());

        log.info("Usuário id={} criando evento no clube id={}", userId, clubId);

        EventResponse response = eventService.createEvent(userId, clubId, request);

        log.info("Evento id={} criado com sucesso no clube id={}", response.id(), clubId);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/clubs/{clubId}/events")
    @Operation(summary = "Lista os eventos de um clube")
    public ResponseEntity<List<EventResponse>> getEventsByClub(JwtAuthenticationToken authentication, @PathVariable Long clubId) {
        Long userId = extractUserId(authentication.getToken());

        log.info("Usuário id={} listando eventos do clube id={}", userId, clubId);

        return ResponseEntity.ok(eventService.getEventsByClub(userId, clubId));
    }

    @GetMapping("/events/{eventId}")
    @Operation(summary = "Retorna os detalhes de um evento")
    public ResponseEntity<EventResponse> getEventById(JwtAuthenticationToken authentication, @PathVariable Long eventId) {
        Long userId = extractUserId(authentication.getToken());

        log.info("Usuário id={} consultando evento id={}", userId, eventId);

        return ResponseEntity.ok(eventService.getEventById(userId, eventId));
    }

    @PatchMapping("/events/{eventId}/status")
    @Operation(summary = "Atualiza o status de um evento (somente ADMIN)")
    public ResponseEntity<EventResponse> updateStatus(JwtAuthenticationToken authentication, @PathVariable Long eventId, @Valid @RequestBody UpdateEventStatusRequest request) {
        Long userId = extractUserId(authentication.getToken());

        log.info("Usuário id={} atualizando status do evento id={} para {}", userId, eventId, request.status());

        EventResponse response = eventService.updateStatus(userId, eventId, request);

        log.info("Status do evento id={} atualizado para {}", eventId, request.status());

        return ResponseEntity.ok(response);
    }

    private Long extractUserId(Jwt jwt) {
        return Long.valueOf(jwt.getSubject());
    }
}