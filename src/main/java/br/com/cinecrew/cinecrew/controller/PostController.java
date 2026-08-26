package br.com.cinecrew.cinecrew.controller;

import br.com.cinecrew.cinecrew.dto.request.CreatePostRequest;
import br.com.cinecrew.cinecrew.dto.response.PostResponse;
import br.com.cinecrew.cinecrew.service.PostService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Slf4j
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Memórias", description = "Feed de fotos vinculadas aos eventos do clube")
public class PostController {

    private final PostService postService;

    @PostMapping("/events/{eventId}/posts")
    @Operation(summary = "Publica uma memória (foto) vinculada a um evento")
    public ResponseEntity<PostResponse> createPost(JwtAuthenticationToken authentication, @PathVariable Long eventId, @Valid @RequestBody CreatePostRequest request) {
        Long userId = extractUserId(authentication.getToken());

        log.info("Usuário id={} publicando memória no evento id={}", userId, eventId);

        PostResponse response = postService.createPost(userId, eventId, request);

        log.info("Memória id={} publicada com sucesso no evento id={}", response.id(), eventId);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/clubs/{clubId}/feed")
    @Operation(summary = "Lista o feed de memórias do clube, paginado")
    public ResponseEntity<Page<PostResponse>> getClubFeed(JwtAuthenticationToken authentication, @PathVariable Long clubId, @ParameterObject Pageable pageable) {
        Long userId = extractUserId(authentication.getToken());

        log.info("Usuário id={} consultando feed do clube id={}", userId, clubId);

        return ResponseEntity.ok(postService.getClubFeed(userId, clubId, pageable));
    }

    @GetMapping("/posts/{postId}")
    @Operation(summary = "Retorna os detalhes de uma memória")
    public ResponseEntity<PostResponse> getPostById(JwtAuthenticationToken authentication, @PathVariable Long postId) {
        Long userId = extractUserId(authentication.getToken());

        log.info("Usuário id={} consultando memória id={}", userId, postId);

        return ResponseEntity.ok(postService.getPostById(userId, postId));
    }

    @DeleteMapping("/posts/{postId}")
    @Operation(summary = "Remove uma memória (autor ou ADMIN do clube)")
    public ResponseEntity<Void> deletePost(JwtAuthenticationToken authentication, @PathVariable Long postId) {
        Long userId = extractUserId(authentication.getToken());

        log.info("Usuário id={} removendo memória id={}", userId, postId);

        postService.deletePost(userId, postId);

        log.info("Memória id={} removida com sucesso", postId);

        return ResponseEntity.noContent().build();
    }

    private Long extractUserId(Jwt jwt) {
        return Long.valueOf(jwt.getSubject());
    }
}