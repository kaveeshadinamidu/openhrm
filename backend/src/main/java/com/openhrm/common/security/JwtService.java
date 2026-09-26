package com.openhrm.common.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

@Service
public class JwtService {

    private static final String CLAIM_ORG = "org";
    private static final String CLAIM_EMPLOYEE = "employeeId";
    private static final String CLAIM_ROLE = "role";

    private final SecretKey key;
    private final long accessTokenTtlMinutes;

    public JwtService(@Value("${openhrm.jwt.secret}") String secret,
                       @Value("${openhrm.jwt.access-token-ttl-minutes}") long accessTokenTtlMinutes) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.accessTokenTtlMinutes = accessTokenTtlMinutes;
    }

    public String generateAccessToken(UserPrincipal principal) {
        Instant now = Instant.now();
        return Jwts.builder()
                .subject(principal.getUsername())
                .claims(Map.of(
                        CLAIM_ORG, principal.getOrganizationId().toString(),
                        CLAIM_EMPLOYEE, principal.getEmployeeId() == null ? "" : principal.getEmployeeId().toString(),
                        CLAIM_ROLE, principal.getRole()
                ))
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plus(accessTokenTtlMinutes, ChronoUnit.MINUTES)))
                .signWith(key)
                .compact();
    }

    public Claims parse(String token) {
        return Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload();
    }

    public String extractEmail(Claims claims) {
        return claims.getSubject();
    }

    public UUID extractOrganizationId(Claims claims) {
        return UUID.fromString(claims.get(CLAIM_ORG, String.class));
    }
}
