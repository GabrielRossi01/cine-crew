package br.com.cinecrew.cinecrew.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class StaticResourceConfig implements WebMvcConfigurer {

    private final String storageDirectory;

    public StaticResourceConfig(@Value("${app.storage.local-directory:uploads}") String storageDirectory) {
        this.storageDirectory = storageDirectory;
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String location = storageDirectory
                .replace("\\", "/")
                .replaceAll("/$", "");

        registry
                .addResourceHandler("/uploads/**")
                .addResourceLocations(
                        "file:" + location + "/"
                );
    }
}