package br.com.cinecrew.cinecrew.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
@RequiredArgsConstructor
public class TmdbConfig {

    private final TmdbProperties tmdbProperties;

    @Bean
    public RestClient tmdbRestClient() {
        return RestClient.builder()
                .baseUrl(tmdbProperties.baseUrl())
                .defaultHeader("Authorization", "Bearer " + tmdbProperties.accessToken())
                .defaultHeader("Accept", "application/json")
                .build();
    }
}
