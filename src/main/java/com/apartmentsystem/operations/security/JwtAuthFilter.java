package com.apartmentsystem.operations.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.List;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    @Value("${jwt.gateway-public-key:}")
    private String gatewayPublicKey;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {

        String path = request.getServletPath();

        return path.equals("/actuator/health")
                || path.equals("/actuator/info")
                || path.equals("/health")
                || path.startsWith("/swagger-ui")
                || path.startsWith("/v3/api-docs");
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        String authorizationHeader = request.getHeader("Authorization");

        // No JWT
        if (authorizationHeader == null
                || !authorizationHeader.startsWith("Bearer ")) {

            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        // Gateway public key not configured yet
        if (gatewayPublicKey == null || gatewayPublicKey.isBlank()) {

            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        try {

            // 1. Extract JWT
            String token = authorizationHeader.substring(7);

            // 2. Convert Gateway public key
            PublicKey publicKey = parsePublicKey(gatewayPublicKey);

            // 3. Verify JWT using Gateway public key
            Claims claims = Jwts.parser()
                    .verifyWith(publicKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            // 4. Check token type
            String type = claims.get("type", String.class);

            if (!"user".equals(type) && !"service".equals(type)) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }

            // 5. Get subject (user ID or service name)
            String subject = claims.getSubject();

            if (subject == null || subject.isBlank()) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }

            // 6. Get roles
            List<SimpleGrantedAuthority> authorities = List.of();

            if ("user".equals(type)) {

                List<?> roles = claims.get("roles", List.class);

                if (roles != null) {
                    authorities = roles.stream()
                            .map(role -> new SimpleGrantedAuthority(
                                    "ROLE_" + role.toString()
                            ))
                            .toList();
                }
            }

            // 7. Create authenticated user
            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(
                            subject,
                            null,
                            authorities
                    );

            // 8. Store authentication in Spring Security
            SecurityContextHolder.getContext()
                    .setAuthentication(authentication);

            // 9. Continue to controller
            filterChain.doFilter(request, response);

        } catch (Exception e) {

            // Invalid / expired / malformed JWT
            SecurityContextHolder.clearContext();

            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        }
    }

    private PublicKey parsePublicKey(String pemPublicKey) throws Exception {

        String encodedKey = pemPublicKey
                .replace("-----BEGIN PUBLIC KEY-----", "")
                .replace("-----END PUBLIC KEY-----", "")
                .replaceAll("\\s", "");

        byte[] decodedKey = Base64.getDecoder().decode(encodedKey);

        X509EncodedKeySpec keySpec =
                new X509EncodedKeySpec(decodedKey);

        return KeyFactory
                .getInstance("RSA")
                .generatePublic(keySpec);
    }
}