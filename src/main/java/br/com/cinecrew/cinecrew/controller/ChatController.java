package br.com.cinecrew.cinecrew.controller;

import br.com.cinecrew.cinecrew.service.ChatService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

import java.util.Objects;

@RestController
@RequestMapping("/chat")
@RequiredArgsConstructor
@Slf4j
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Chat", description = "Assistente virtual Cineco powered by Gemini AI")
public class ChatController {

    private final ChatService chatService;

    @PostMapping(produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @Operation(summary = "Envia uma mensagem para o Cineco e recebe resposta via Server-Sent Events")
    public Flux<String> chat(JwtAuthenticationToken authentication, @RequestBody ChatRequest request) {
        Long userId = extractUserId(authentication.getToken());

        log.info("Usuário id={} enviando mensagem para o Cineco", userId);

        return chatService.sendMessage(request.message(), userId.toString());
    }

    @DeleteMapping("/history")
    @Operation(summary = "Limpa o histórico de conversa do usuário com o Cineco")
    public ResponseEntity<Void> clearHistory(JwtAuthenticationToken authentication) {
        Long userId = extractUserId(authentication.getToken());

        log.info("Usuário id={} limpando histórico de conversa", userId);

        return ResponseEntity.noContent().build();
    }

    private Long extractUserId(Jwt jwt) {
        return Long.valueOf(Objects.requireNonNull(jwt.getSubject()));
    }

    public record ChatRequest(String message) {}
}