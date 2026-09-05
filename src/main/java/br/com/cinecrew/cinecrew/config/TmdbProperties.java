package br.com.cinecrew.cinecrew.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "tmdb")
public record TmdbProperties(
        String baseUrl,
        String accessToken,
        String imageBaseUrl
) {}