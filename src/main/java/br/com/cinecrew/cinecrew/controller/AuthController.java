package br.com.cinecrew.cinecrew.controller;

import br.com.cinecrew.cinecrew.dto.request.LoginRequest;
import br.com.cinecrew.cinecrew.dto.request.RegisterRequest;
import br.com.cinecrew.cinecrew.dto.response.AuthResponse;
import br.com.cinecrew.cinecrew.dto.response.UserSummaryResponse;
import br.com.cinecrew.cinecrew.service.AuthService;
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
@RequestMapping("/auth")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Auth", description = "Cadastro, login e sessão do usuário")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    @Operation(summary = "Cadastra um novo usuário com email e senha")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        log.info("Recebida solicitação de cadastro para o email={}", request.email());

        AuthResponse response = authService.register(request);

        log.info("Cadastro concluído com sucesso para o email={}", request.email());

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/login")
    @Operation(summary = "Autentica um usuário com email e senha")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        log.info("Tentativa de login para o email={}", request.email());

        AuthResponse response = authService.login(request);

        log.info("Login bem-sucedido para o email={}", request.email());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/me")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Retorna os dados do usuário autenticado")
    public ResponseEntity<UserSummaryResponse> getCurrentUser(JwtAuthenticationToken authentication) {
        Long userId = extractUserId(authentication.getToken());

        log.info("Consultando dados do usuário autenticado id={}", userId);

        return ResponseEntity.ok(authService.getCurrentUser(userId));
    }

    private Long extractUserId(Jwt jwt) {
        return Long.valueOf(jwt.getSubject());
    }
}