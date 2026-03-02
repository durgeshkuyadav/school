package com.school.gateway.filter;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Component
public class JwtAuthFilter extends AbstractGatewayFilterFactory<JwtAuthFilter.Config> {

    @Value("${app.jwt.secret}")
    private String jwtSecret;

    private final List<String> publicPaths = List.of(
        "/auth/login", "/auth/refresh", "/teachers/public", "/calendar/public"
    );

    public JwtAuthFilter() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            String path = exchange.getRequest().getPath().toString();

            if (isPublicPath(path)) {
                return chain.filter(exchange);
            }

            String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return unauthorized(exchange, "Missing or invalid Authorization header");
            }

            String token = authHeader.substring(7);
            try {
                Claims claims = validateToken(token);
                ServerHttpRequest mutatedRequest = exchange.getRequest().mutate()
                    .header("X-User-Id", claims.getSubject())
                    .header("X-User-Role", claims.get("role", String.class))
                    .header("X-Class-Id", claims.get("classId", String.class) != null
                        ? claims.get("classId", String.class) : "")
                    .header("X-Subject-Ids", claims.get("subjectIds", String.class) != null
                        ? claims.get("subjectIds", String.class) : "")
                    .build();
                return chain.filter(exchange.mutate().request(mutatedRequest).build());
            } catch (Exception e) {
                return unauthorized(exchange, "Invalid or expired token");
            }
        };
    }

    private Claims validateToken(String token) {
        SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
        return Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload();
    }

    private boolean isPublicPath(String path) {
        return publicPaths.stream().anyMatch(path::contains);
    }

    private Mono<Void> unauthorized(ServerWebExchange exchange, String message) {
        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
        exchange.getResponse().getHeaders().add("Content-Type", "application/json");
        var buffer = exchange.getResponse().bufferFactory()
            .wrap(("{\"error\":\"" + message + "\"}").getBytes());
        return exchange.getResponse().writeWith(Mono.just(buffer));
    }

    public static class Config {}
}
