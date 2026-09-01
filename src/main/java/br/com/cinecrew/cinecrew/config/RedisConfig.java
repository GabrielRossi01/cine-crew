package br.com.cinecrew.cinecrew.config;

import org.springframework.boot.cache.autoconfigure.RedisCacheManagerBuilderCustomizer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJacksonJsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import tools.jackson.databind.jsontype.BasicPolymorphicTypeValidator;
import tools.jackson.databind.jsontype.PolymorphicTypeValidator;

import java.time.Duration;

@Configuration
@EnableCaching
public class RedisConfig {

    private static final String CLUB_RANKINGS_CACHE = "club-rankings";

    private static final PolymorphicTypeValidator POLY_TYPE_VALIDATOR =
            BasicPolymorphicTypeValidator.builder()
                    .allowIfSubType("br.com.cinecrew")
                    .allowIfSubType("java.lang")
                    .allowIfSubType("java.util")
                    .allowIfSubType("java.time")
                    .build();

    private GenericJacksonJsonRedisSerializer jsonSerializer() {
        return GenericJacksonJsonRedisSerializer.builder()
                .enableDefaultTyping(POLY_TYPE_VALIDATOR)
                .build();
    }

    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(jsonSerializer());
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setHashValueSerializer(jsonSerializer());
        template.afterPropertiesSet();
        return template;
    }

    @Bean
    public RedisCacheManagerBuilderCustomizer redisCacheManagerBuilderCustomizer() {
        return builder -> builder
                .cacheDefaults(defaultCacheConfig())
                .withCacheConfiguration(CLUB_RANKINGS_CACHE, defaultCacheConfig().entryTtl(Duration.ofMinutes(10)));
    }

    private RedisCacheConfiguration defaultCacheConfig() {
        return RedisCacheConfiguration.defaultCacheConfig()
                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(jsonSerializer()))
                .disableCachingNullValues();
    }
}