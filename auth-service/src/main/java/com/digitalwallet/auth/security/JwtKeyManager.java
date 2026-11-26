package com.digitalwallet.auth.security;

import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.KeyUse;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtIssuerValidator;
import org.springframework.security.oauth2.jwt.JwtTimestampValidator;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.UUID;

@Component
public class JwtKeyManager {
    
    private final RSAKey rsaKey;
    private final JWKSet jwkSet;
    private final JwtEncoder jwtEncoder;
    private final JwtDecoder jwtDecoder;

    public JwtKeyManager(@Value("${app.jwt.issuer}") String issuer) {
        this.rsaKey = generateRsaKey();
        this.jwkSet = new JWKSet(this.rsaKey);
        this.jwtEncoder = new NimbusJwtEncoder(new ImmutableJWKSet<>(this.jwkSet));

        try {
            NimbusJwtDecoder decoder = NimbusJwtDecoder.withPublicKey(this.rsaKey.toRSAPublicKey()).build();
            OAuth2TokenValidator<Jwt> validator = new DelegatingOAuth2TokenValidator<>(
                new JwtTimestampValidator(),
                new JwtIssuerValidator(issuer)
            );
            decoder.setJwtValidator(validator);
            this.jwtDecoder = decoder;
        } catch (Exception ex) {
            throw new IllegalStateException("Failed to initialize JwtDecoder", ex);
        }
        
    }

    public JwtEncoder jwtEncoder() {
        return jwtEncoder;
    }

    public JwtDecoder jwtDecoder() {
        return jwtDecoder;
    }

    public Map<String, Object> publicJwkSetJson() {
        return jwkSet.toJSONObject();
    }

    private static RSAKey generateRsaKey() {
        try {
            KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
            generator.initialize(2048);
            KeyPair keyPair = generator.generateKeyPair();
            return new RSAKey.Builder((RSAPublicKey) keyPair.getPublic())
                    .privateKey((RSAPrivateKey) keyPair.getPrivate())
                    .keyUse(KeyUse.SIGNATURE)
                    .algorithm(JWSAlgorithm.RS256)
                    .keyID(UUID.randomUUID().toString())
                    .build();
        } catch (Exception ex) {
            throw new IllegalStateException("Failed to generate RSA key pair", ex);
        }
    }
    
}
