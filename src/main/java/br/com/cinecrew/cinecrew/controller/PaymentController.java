package br.com.cinecrew.cinecrew.controller;

import br.com.cinecrew.cinecrew.dto.request.AddParticipantRequest;
import br.com.cinecrew.cinecrew.dto.response.EventBalanceResponse;
import br.com.cinecrew.cinecrew.service.PaymentService;
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

@RestController
@RequestMapping("/events/{eventId}")
@RequiredArgsConstructor
@Slf4j
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Pagamentos", description = "Rachadinha, confirmação de Pix e saldo do evento")
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/participants")
    @Operation(summary = "Adiciona um participante à rachadinha (somente ADMIN)")
    public ResponseEntity<Void> addParticipant(JwtAuthenticationToken authentication, @PathVariable Long eventId, @Valid @RequestBody AddParticipantRequest request) {
        Long userId = extractUserId(authentication.getToken());

        log.info("Usuário id={} adicionando participante id={} ao evento id={}", userId, request.userId(), eventId);

        paymentService.addParticipant(userId, eventId, request);

        log.info("Participante id={} adicionado ao evento id={}", request.userId(), eventId);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PatchMapping("/participants/{participantUserId}/pay")
    @Operation(summary = "Confirma o pagamento de um participante")
    public ResponseEntity<Void> confirmPayment(JwtAuthenticationToken authentication, @PathVariable Long eventId, @PathVariable Long participantUserId) {
        Long userId = extractUserId(authentication.getToken());

        log.info("Usuário id={} confirmando pagamento do participante id={} no evento id={}", userId, participantUserId, eventId);

        paymentService.confirmPayment(userId, eventId, participantUserId);

        log.info("Pagamento confirmado para o participante id={} no evento id={}", participantUserId, eventId);

        return ResponseEntity.ok().build();
    }

    @GetMapping("/balance")
    @Operation(summary = "Retorna o saldo consolidado do evento: quem pagou e quem falta pagar")
    public ResponseEntity<EventBalanceResponse> getBalance(JwtAuthenticationToken authentication, @PathVariable Long eventId) {
        Long userId = extractUserId(authentication.getToken());

        log.info("Usuário id={} consultando saldo do evento id={}", userId, eventId);

        return ResponseEntity.ok(paymentService.getBalance(userId, eventId));
    }

    private Long extractUserId(Jwt jwt) {
        return Long.valueOf(jwt.getSubject());
    }
}