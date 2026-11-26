package com.digitalwallet.auth.security;

import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController 
public class JwksController {
    
    private final JwtKeyManager keyManager;

    public JwksController(JwtKeyManager keyManager) {
        this.keyManager = keyManager;
    }

    @GetMapping("/.well-known/jwks.json")
    public Map<String, Object> jwks() {
        return keyManager.publicJwkSetJson();
    }
}
