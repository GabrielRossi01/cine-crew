package br.com.cinecrew.cinecrew.config;

import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtValidators;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;

@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(RsaKeyProperties.class)
public class JwtConfig {

    private final RsaKeyProperties rsaKeys;
    private final String issuer;

    public JwtConfig(RsaKeyProperties rsaKeys, @Value("${app.security.jwt.issuer:cinecrew}") String issuer) {
        this.rsaKeys = rsaKeys;
        this.issuer = issuer;
    }

    @Bean
    public JwtEncoder jwtEncoder() {
        JWK rsaKey = new RSAKey.Builder(rsaKeys.publicKey())
                .privateKey(rsaKeys.privateKey())
                .build();

        JWKSource<SecurityContext> jwkSource = new ImmutableJWKSet<>(new JWKSet(rsaKey));

        return new NimbusJwtEncoder(jwkSource);
    }

    @Bean
    public JwtDecoder jwtDecoder() {
        NimbusJwtDecoder decoder = NimbusJwtDecoder
                .withPublicKey(rsaKeys.publicKey())
                .build();

        decoder.setJwtValidator(JwtValidators.createDefaultWithIssuer(issuer));

        return decoder;
    }
}