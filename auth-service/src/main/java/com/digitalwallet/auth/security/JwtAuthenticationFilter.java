package com.digitalwallet.auth.security;

import java.io.IOException;
import java.util.List;

import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import org.slf4j.MDC;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtException;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    public JwtAuthenticationFilter(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)
            throws ServletException, IOException {
        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        boolean setMdcUser = false;

        try {
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String token = authHeader.substring(7);
                try {
                    Jwt jwt = jwtService.decodeAndValidate(token);
                    List<String> roles = jwt.getClaimAsStringList("roles");
                    List<SimpleGrantedAuthority> authorities = roles == null ? List.of() :
                        roles.stream().map(r -> new SimpleGrantedAuthority("ROLE_" + r)).toList();
                    var auth = new UsernamePasswordAuthenticationToken(jwt, null, authorities);
                    SecurityContextHolder.getContext().setAuthentication(auth);
                    MDC.put("userId", jwt.getSubject());
                    setMdcUser = true;
                } catch (JwtException ex) {
                    SecurityContextHolder.clearContext();
                }
            }
            filterChain.doFilter(request, response);
        } finally {
            if (setMdcUser) {
                MDC.remove("userId");
            }
        }
    }

}
