
package com.inventorymanagement.inventory.config;

import java.security.KeyPair;
import java.security.KeyPairGenerator;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;

@Configuration
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public KeyPair keyPair() {

        try {

            KeyPairGenerator keyPairGenerator =
                    KeyPairGenerator.getInstance("RSA");

            keyPairGenerator.initialize(2048);

            return keyPairGenerator.generateKeyPair();

        } catch (Exception e) {

            throw new RuntimeException(
                    "Failed to generate RSA key pair", e
            );
        }
    }

    @Bean
public JwtEncoder jwtEncoder(KeyPair keyPair) {

    RSAKey rsaKey = new RSAKey.Builder(
            (java.security.interfaces.RSAPublicKey)
                    keyPair.getPublic()
    )
            .privateKey(
                    (java.security.interfaces.RSAPrivateKey)
                            keyPair.getPrivate()
            )
            .build();

    JWKSource<com.nimbusds.jose.proc.SecurityContext> jwkSource =
            new ImmutableJWKSet<>(
                    new JWKSet(rsaKey)
            );

    return new NimbusJwtEncoder(jwkSource);
}

    @Bean
    public JwtDecoder jwtDecoder(KeyPair keyPair) {

        return NimbusJwtDecoder
                .withPublicKey(
                        (java.security.interfaces.RSAPublicKey)
                                keyPair.getPublic()
                )
                .build();
    }

    @Bean
    public org.springframework.security.web.SecurityFilterChain securityFilterChain(
            HttpSecurity http) throws Exception {

        http
                .csrf(csrf -> csrf.disable())

                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/v1/auth/**").permitAll()
                        .anyRequest().permitAll()
                )

                .oauth2ResourceServer(
                        oauth2 -> oauth2.jwt(jwt -> {})
                );

        return http.build();
    }
}

