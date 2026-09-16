package br.com.cinecrew.cinecrew.service;

import br.com.cinecrew.cinecrew.dto.request.LoginRequest;
import br.com.cinecrew.cinecrew.dto.request.RegisterRequest;
import br.com.cinecrew.cinecrew.dto.response.AuthResponse;
import br.com.cinecrew.cinecrew.dto.response.UserSummaryResponse;
import br.com.cinecrew.cinecrew.exception.BusinessRuleException;
import br.com.cinecrew.cinecrew.exception.DuplicateResourceException;
import br.com.cinecrew.cinecrew.exception.ResourceNotFoundException;
import br.com.cinecrew.cinecrew.mapper.UserMapper;
import br.com.cinecrew.cinecrew.model.User;
import br.com.cinecrew.cinecrew.model.enums.AvatarSource;
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
    private final UserMapper userMapper;

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        String normalizedEmail = normalizeEmail(request.email());

        if (userRepository.existsByEmail(normalizedEmail)) {
            throw new DuplicateResourceException("Já existe uma conta vinculada a este email");
        }

        User user = User.builder()
                .name(request.name().trim())
                .username(generateInitialUsername(request.name()))
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
                .orElseGet(() -> userRepository.save(
                        User.builder()
                                .name(name)
                                .username(generateInitialUsername(name))
                                .email(email)
                                .googleId(googleId)
                                .avatarUrl(avatarUrl)
                                .avatarSource(
                                        avatarUrl == null || avatarUrl.isBlank()
                                                ? AvatarSource.NONE
                                                : AvatarSource.GOOGLE
                                )
                                .build()
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
                                .username(generateInitialUsername(name))
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

        boolean hasCustomAvatar = existingUser.getAvatarSource() == AvatarSource.UPLOAD;

        if (!hasCustomAvatar && avatarUrl != null && !avatarUrl.isBlank()) {
            existingUser.setAvatarUrl(avatarUrl);
            existingUser.setAvatarSource(AvatarSource.GOOGLE);
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

    public UserSummaryResponse getCurrentUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário", userId));

        return userMapper.toSummaryResponse(user);
    }

    private String generateInitialUsername(String name) {
        String baseUsername = name
                .trim()
                .toLowerCase(Locale.ROOT)
                .replaceAll("[^a-z0-9]+", ".")
                .replaceAll("^\\.|\\.$", "");

        if (baseUsername.length() < 3) {
            baseUsername = "user";
        }

        baseUsername = baseUsername.substring(
                0,
                Math.min(baseUsername.length(), 25)
        );

        String username = baseUsername;
        int suffix = 1;

        while (userRepository.existsByUsername(username)) {
            suffix++;

            String suffixValue = String.valueOf(suffix);
            int maxBaseLength = 30 - suffixValue.length() - 1;

            username = baseUsername.substring(
                    0,
                    Math.min(baseUsername.length(), maxBaseLength)
            ) + "." + suffixValue;
        }

        return username;
    }
}