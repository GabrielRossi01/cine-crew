package br.com.cinecrew.cinecrew.security;

import br.com.cinecrew.cinecrew.dto.response.AuthResponse;
import br.com.cinecrew.cinecrew.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class OAuth2LoginSuccessHandler implements AuthenticationSuccessHandler {

    private final AuthService authService;

    @Value("${app.frontend-url:http://localhost:4200}")
    private String frontendUrl;

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ) throws IOException {

        OAuth2User googleUser = (OAuth2User) authentication.getPrincipal();

        AuthResponse authResponse = authService.loginWithGoogle(
                googleUser.getAttribute("sub"),
                googleUser.getAttribute("email"),
                googleUser.getAttribute("name"),
                googleUser.getAttribute("picture")
        );

        String redirectUrl = UriComponentsBuilder
                .fromUriString(frontendUrl)
                .path("/oauth2/redirect")
                .queryParam("token", authResponse.token())
                .queryParam("expiresIn", authResponse.expiresInSeconds())
                .build()
                .toUriString();

        response.sendRedirect(redirectUrl);
    }
}