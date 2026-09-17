package br.com.cinecrew.cinecrew.controller;

import br.com.cinecrew.cinecrew.service.ChatService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;

@RestController
@RequestMapping("/chat")
@RequiredArgsConstructor
@Slf4j
@SecurityRequirement(name = "bearerAuth")
@Tag(
        name = "Chat",
        description = "Assistente virtual Cineco powered by Gemini AI"
)
public class ChatController {

    private final ChatService chatService;

    @PostMapping(
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @Operation(summary = "Envia uma mensagem para o Cineco")
    public ResponseEntity<ChatResponse> chat(JwtAuthenticationToken authentication, @Valid @RequestBody ChatRequest request) {
        Long userId = extractUserId(authentication.getToken());

        log.info("Usuário id={} enviando mensagem para o Cineco", userId);

        String reply = chatService.sendMessage(request.message(), userId.toString());

        return ResponseEntity.ok(new ChatResponse(reply));
    }

    @DeleteMapping(value = "/history", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Limpa o histórico de conversa do usuário com o Cineco")
    public ResponseEntity<Void> clearHistory(JwtAuthenticationToken authentication) {
        Long userId = extractUserId(authentication.getToken());

        log.info("Usuário id={} limpando histórico de conversa", userId);

        return ResponseEntity.noContent().build();
    }

    private Long extractUserId(Jwt jwt) {
        return Long.valueOf(Objects.requireNonNull(jwt.getSubject(), "JWT subject não pode ser nulo"));
    }

    public record ChatRequest(@NotBlank(message = "A mensagem não pode estar vazia") String message) { }

    public record ChatResponse(String reply) {}
}