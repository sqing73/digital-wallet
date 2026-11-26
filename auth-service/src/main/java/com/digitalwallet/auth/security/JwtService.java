package com.digitalwallet.auth.security;

import java.time.Instant;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jose.jws.SignatureAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

@Service 
public class JwtService {

    private final JwtKeyManager keyManager;
    private final String issuer;
    private final long expirationSeconds;

    public JwtService(JwtKeyManager keyManager,
                      @Value("${app.jwt.issuer}") String issuer,
                      @Value("${app.jwt.expiration-seconds:1800}") long expirationSeconds
    ) {
        this.keyManager = keyManager;
        this.issuer = issuer;
        this.expirationSeconds = expirationSeconds;
    }
    
    public String issueAccessToken(String userId, String username, String displayName, List<String> roles) {
        Instant now = Instant.now();
        JwsHeader header = JwsHeader.with(SignatureAlgorithm.RS256).build();
        JwtClaimsSet claims = JwtClaimsSet.builder()
                                          .issuer(issuer)
                                          .issuedAt(now)
                                          .subject(userId)
                                          .expiresAt(now.plusSeconds(expirationSeconds))
                                          .claim("username", username)
                                          .claim("name", displayName)
                                          .claim("roles", roles)
                                          .build();
        return keyManager.jwtEncoder().encode(JwtEncoderParameters.from(header, claims)).getTokenValue();
                                          
    }

    public Jwt decodeAndValidate(String token) {
        return keyManager.jwtDecoder().decode(token);
    }

    public long expirationSeconds() {
        return expirationSeconds;
    }
}
