package br.com.cinecrew.cinecrew.controller;

import br.com.cinecrew.cinecrew.dto.request.UpdateProfileRequest;
import br.com.cinecrew.cinecrew.dto.response.UserProfileResponse;
import br.com.cinecrew.cinecrew.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Objects;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Tag(name = "Usuários", description = "Consulta e gerenciamento do perfil do usuário autenticado")
@SecurityRequirement(name = "bearerAuth")
public class UserController {

    private final UserService userService;

    @GetMapping("/me")
    @Operation(
            summary = "Consulta o perfil atual",
            description = "Retorna os dados do usuário associado ao token JWT enviado na requisição."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Perfil retornado com sucesso",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = UserProfileResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Token ausente, inválido ou expirado",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Usuário não encontrado",
                    content = @Content
            )
    })
    public ResponseEntity<UserProfileResponse> getCurrentProfile(
            JwtAuthenticationToken authentication
    ) {
        Long userId = extractUserId(authentication);

        return ResponseEntity.ok(userService.getCurrentProfile(userId));
    }

    @PatchMapping("/me")
    @Operation(
            summary = "Atualiza o perfil atual",
            description = "Altera o nome de exibição e o username do usuário autenticado."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Perfil atualizado com sucesso",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = UserProfileResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Dados inválidos ou username fora do formato permitido",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Token ausente, inválido ou expirado",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Usuário não encontrado",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Username já está sendo utilizado",
                    content = @Content
            )
    })
    public ResponseEntity<UserProfileResponse> updateCurrentProfile(
            JwtAuthenticationToken authentication,
            @Valid @RequestBody UpdateProfileRequest request
    ) {
        Long userId = extractUserId(authentication);

        return ResponseEntity.ok(userService.updateCurrentProfile(userId, request));
    }

    @PostMapping(
            value = "/me/avatar",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    @Operation(
            summary = "Envia uma foto de perfil",
            description = """
                    Envia uma imagem para ser utilizada como avatar do usuário autenticado.
                    
                    Formatos aceitos: JPG, PNG e WEBP.
                    Tamanho máximo recomendado: 5 MB.
                    
                    Caso o usuário possua uma foto anterior, ela será substituída.
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Avatar enviado com sucesso",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = UserProfileResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Arquivo ausente, formato inválido ou tamanho excedido",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Token ausente, inválido ou expirado",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Usuário não encontrado",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "413",
                    description = "Arquivo excede o tamanho máximo permitido",
                    content = @Content
            )
    })
    public ResponseEntity<UserProfileResponse> uploadCurrentAvatar(
            JwtAuthenticationToken authentication,
            @Parameter(
                    description = "Imagem de perfil nos formatos JPG, PNG ou WEBP",
                    required = true,
                    content = @Content(
                            mediaType = MediaType.APPLICATION_OCTET_STREAM_VALUE,
                            schema = @Schema(
                                    type = "string",
                                    format = "binary"
                            )
                    )
            )
            @RequestParam("file") MultipartFile file
    ) {
        Long userId = extractUserId(authentication);

        return ResponseEntity.ok(userService.uploadCurrentAvatar(userId, file)
        );
    }

    @DeleteMapping("/me/avatar")
    @Operation(
            summary = "Remove a foto de perfil",
            description = "Remove o avatar personalizado do usuário autenticado."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "204",
                    description = "Avatar removido com sucesso"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Token ausente, inválido ou expirado",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Usuário não encontrado",
                    content = @Content
            )
    })
    public ResponseEntity<Void> removeCurrentAvatar(JwtAuthenticationToken authentication) {
        Long userId = extractUserId(authentication);

        userService.removeCurrentAvatar(userId);

        return ResponseEntity.noContent().build();
    }

    private Long extractUserId(JwtAuthenticationToken authentication) {
        Jwt jwt = authentication.getToken();

        return Long.valueOf(Objects.requireNonNull(jwt.getSubject()));
    }
}