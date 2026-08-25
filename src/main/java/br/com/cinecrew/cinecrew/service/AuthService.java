package br.com.cinecrew.cinecrew.service;

import br.com.cinecrew.cinecrew.dto.request.LoginRequest;
import br.com.cinecrew.cinecrew.dto.request.RegisterRequest;
import br.com.cinecrew.cinecrew.dto.response.AuthResponse;
import br.com.cinecrew.cinecrew.exception.BusinessRuleException;
import br.com.cinecrew.cinecrew.exception.DuplicateResourceException;
import br.com.cinecrew.cinecrew.model.User;
import br.com.cinecrew.cinecrew.repository.UserRepository;
import br.com.cinecrew.cinecrew.security.SecurityUser;
import br.com.cinecrew.cinecrew.security.TokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final TokenService tokenService;

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        String normalizedEmail = normalizeEmail(request.email());

        if (userRepository.existsByEmail(normalizedEmail)) {
            throw new DuplicateResourceException("Já existe uma conta vinculada a este email");
        }

        User user = User.builder()
                .name(request.name().trim())
                .email(normalizedEmail)
                .passwordHash(passwordEncoder.encode(request.password()))
                .build();

        User savedUser = userRepository.save(user);

        return createAuthResponse(savedUser);
    }

    public AuthResponse login(LoginRequest request) {
        String normalizedEmail = normalizeEmail(request.email());

        Authentication authentication = authenticationManager.authenticate(
                UsernamePasswordAuthenticationToken.unauthenticated(
                        normalizedEmail,
                        request.password()
                )
        );

        SecurityUser securityUser = (SecurityUser) authentication.getPrincipal();

        return createAuthResponse(securityUser.getUser());
    }

    @Transactional
    public AuthResponse loginWithGoogle(String googleId, String email, String name, String avatarUrl) {
        validateGoogleAttributes(googleId, email, name);

        String normalizedEmail = normalizeEmail(email);

        User user = userRepository.findByGoogleId(googleId)
                .orElseGet(() -> findOrCreateGoogleUser(
                        googleId,
                        normalizedEmail,
                        name.trim(),
                        avatarUrl
                ));

        return createAuthResponse(user);
    }

    private User findOrCreateGoogleUser(String googleId, String email, String name, String avatarUrl) {
        return userRepository.findByEmail(email)
                .map(existingUser -> linkGoogleAccount(
                        existingUser,
                        googleId,
                        name,
                        avatarUrl
                ))
                .orElseGet(() -> userRepository.save(
                        User.builder()
                                .name(name)
                                .email(email)
                                .googleId(googleId)
                                .avatarUrl(avatarUrl)
                                .build()
                ));
    }

    private User linkGoogleAccount(User existingUser, String googleId, String name, String avatarUrl) {
        existingUser.setGoogleId(googleId);

        if (existingUser.getName() == null || existingUser.getName().isBlank()) {
            existingUser.setName(name);
        }

        if (avatarUrl != null && !avatarUrl.isBlank()) {
            existingUser.setAvatarUrl(avatarUrl);
        }

        return userRepository.save(existingUser);
    }

    private AuthResponse createAuthResponse(User user) {
        String token = tokenService.generateToken(user);

        return new AuthResponse(
                token,
                tokenService.getExpirationSeconds()
        );
    }

    private String normalizeEmail(String email) {
        return email.trim().toLowerCase(Locale.ROOT);
    }

    private void validateGoogleAttributes(String googleId, String email, String name) {
        if (googleId == null || googleId.isBlank()
                || email == null || email.isBlank()
                || name == null || name.isBlank()) {
            throw new BusinessRuleException("Não foi possível obter os dados obrigatórios da conta Google");
        }
    }
}