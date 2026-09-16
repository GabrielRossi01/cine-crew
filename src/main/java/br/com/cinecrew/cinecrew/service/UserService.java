package br.com.cinecrew.cinecrew.service;

import br.com.cinecrew.cinecrew.dto.request.UpdateProfileRequest;
import br.com.cinecrew.cinecrew.dto.response.UserProfileResponse;
import br.com.cinecrew.cinecrew.exception.DuplicateResourceException;
import br.com.cinecrew.cinecrew.exception.ResourceNotFoundException;
import br.com.cinecrew.cinecrew.mapper.UserMapper;
import br.com.cinecrew.cinecrew.model.User;
import br.com.cinecrew.cinecrew.model.enums.AvatarSource;
import br.com.cinecrew.cinecrew.repository.UserRepository;
import br.com.cinecrew.cinecrew.storage.FileStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.Locale;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final FileStorageService fileStorageService;

    @Transactional(readOnly = true)
    public UserProfileResponse getCurrentProfile(Long userId) {
        User user = findUser(userId);

        return userMapper.toProfileResponse(user);
    }

    @Transactional
    public UserProfileResponse updateCurrentProfile(Long userId, UpdateProfileRequest request) {
        User user = findUser(userId);

        String normalizedUsername = normalizeUsername(request.username());

        if (userRepository.existsByUsernameAndIdNot(normalizedUsername, userId)) {
            throw new DuplicateResourceException(
                    "Este username já está sendo utilizado"
            );
        }

        user.setName(request.name().trim());
        user.setUsername(normalizedUsername);

        User savedUser = userRepository.save(user);

        return userMapper.toProfileResponse(savedUser);
    }

    @Transactional
    public UserProfileResponse uploadCurrentAvatar(Long userId, MultipartFile file) {
        User user = findUser(userId);

        String previousAvatarUrl = user.getAvatarUrl();

        String avatarUrl = fileStorageService.storeAvatar(userId, file);

        user.setAvatarUrl(avatarUrl);
        user.setAvatarSource(AvatarSource.UPLOAD);

        User savedUser = userRepository.save(user);

        if (previousAvatarUrl != null && !previousAvatarUrl.isBlank()) {
            fileStorageService.deleteByUrl(previousAvatarUrl);
        }

        return userMapper.toProfileResponse(savedUser);
    }

    @Transactional
    public void removeCurrentAvatar(Long userId) {
        User user = findUser(userId);

        String previousAvatarUrl = user.getAvatarUrl();

        user.setAvatarUrl(null);
        user.setAvatarSource(AvatarSource.NONE);

        userRepository.save(user);

        if (previousAvatarUrl != null && !previousAvatarUrl.isBlank()) {
            fileStorageService.deleteByUrl(previousAvatarUrl);
        }
    }

    private User findUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Usuário", userId)
                );
    }

    private String normalizeUsername(String username) {
        return username.trim().toLowerCase(Locale.ROOT);
    }
}