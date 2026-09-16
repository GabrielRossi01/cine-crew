package br.com.cinecrew.cinecrew.storage;

import br.com.cinecrew.cinecrew.exception.BusinessRuleException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Set;
import java.util.UUID;

@Service
@Slf4j
public class LocalFileStorageService implements FileStorageService {

    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024;

    private static final Set<String> ALLOWED_CONTENT_TYPES = Set.of(
            "image/jpeg",
            "image/png",
            "image/webp"
    );

    private final Path avatarDirectory;
    private final String publicBaseUrl;

    public LocalFileStorageService(
            @Value("${app.storage.local-directory:uploads}") String storageDirectory,
            @Value("${app.storage.public-base-url:http://localhost:8080/uploads}") String publicBaseUrl
    ) {
        this.avatarDirectory = Paths.get(storageDirectory)
                .toAbsolutePath()
                .normalize();

        this.publicBaseUrl = publicBaseUrl.replaceAll("/$", "");

        createDirectory();
    }

    @Override
    public String storeAvatar(Long userId, MultipartFile file) {
        validateFile(file);

        String extension = resolveExtension(file.getOriginalFilename());
        String filename = "avatar-" + userId + "-" + UUID.randomUUID() + extension;

        Path target = avatarDirectory.resolve(filename).normalize();

        if (!target.startsWith(avatarDirectory)) {
            throw new BusinessRuleException("Nome de arquivo inválido");
        }

        try (InputStream inputStream = file.getInputStream()) {
            Files.copy(
                    inputStream,
                    target,
                    StandardCopyOption.REPLACE_EXISTING
            );

            log.info("Avatar armazenado para userId={} filename={}", userId, filename);

            return publicBaseUrl + "/" + filename;
        } catch (IOException exception) {
            log.error("Erro ao armazenar avatar para userId={}", userId, exception);

            throw new BusinessRuleException("Não foi possível armazenar a imagem");
        }
    }

    @Override
    public void deleteByUrl(String fileUrl) {
        if (fileUrl == null || fileUrl.isBlank()) {
            return;
        }

        if (!fileUrl.startsWith(publicBaseUrl)) {
            return;
        }

        String filename = fileUrl.substring(
                publicBaseUrl.length()
        ).replaceFirst("^/", "");

        Path file = avatarDirectory.resolve(filename).normalize();

        if (!file.startsWith(avatarDirectory)) {
            return;
        }

        try {
            Files.deleteIfExists(file);
        } catch (IOException exception) {
            log.warn("Não foi possível remover arquivo de avatar={}", file, exception);
        }
    }

    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BusinessRuleException("Selecione uma imagem para enviar");
        }

        if (file.getSize() > MAX_FILE_SIZE) {
            throw new BusinessRuleException("A imagem deve ter no máximo 5 MB");
        }

        String contentType = file.getContentType();

        if (contentType == null || !ALLOWED_CONTENT_TYPES.contains(contentType.toLowerCase())) {
            throw new BusinessRuleException("Formato inválido. Use JPG, PNG ou WEBP"
            );
        }
    }

    private String resolveExtension(String originalFilename) {
        String extension = StringUtils.getFilenameExtension(originalFilename);

        if (extension == null || extension.isBlank()) {
            return ".jpg";
        }

        return "." + extension.toLowerCase();
    }

    private void createDirectory() {
        try {
            Files.createDirectories(avatarDirectory);
        } catch (IOException exception) {
            throw new IllegalStateException("Não foi possível criar o diretório de uploads", exception);
        }
    }
}