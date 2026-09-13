package br.com.cinecrew.cinecrew.controller;

import br.com.cinecrew.cinecrew.dto.request.UpdateProfileRequest;
import br.com.cinecrew.cinecrew.dto.response.UserProfileResponse;
import br.com.cinecrew.cinecrew.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/me")
    public ResponseEntity<UserProfileResponse> getCurrentProfile(
            JwtAuthenticationToken authentication
    ) {
        Long userId = extractUserId(authentication);

        return ResponseEntity.ok(
                userService.getCurrentProfile(userId)
        );
    }

    @PatchMapping("/me")
    public ResponseEntity<UserProfileResponse> updateCurrentProfile(
            JwtAuthenticationToken authentication,
            @Valid @RequestBody UpdateProfileRequest request
    ) {
        Long userId = extractUserId(authentication);

        return ResponseEntity.ok(
                userService.updateCurrentProfile(userId, request)
        );
    }

    @DeleteMapping("/me/avatar")
    public ResponseEntity<Void> removeCurrentAvatar(
            JwtAuthenticationToken authentication
    ) {
        Long userId = extractUserId(authentication);

        userService.removeCurrentAvatar(userId);

        return ResponseEntity.noContent().build();
    }

    private Long extractUserId(JwtAuthenticationToken authentication) {
        Jwt jwt = authentication.getToken();

        return Long.valueOf(jwt.getSubject());
    }
}