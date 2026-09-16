package br.com.cinecrew.cinecrew.storage;

import org.springframework.web.multipart.MultipartFile;

public interface FileStorageService {

    String storeAvatar(Long userId, MultipartFile file);

    void deleteByUrl(String fileUrl);
}