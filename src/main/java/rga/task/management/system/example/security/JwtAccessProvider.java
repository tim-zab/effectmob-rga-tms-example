package rga.task.management.system.example.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import rga.task.management.system.example.entities.User;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Slf4j
@Component
public class JwtAccessProvider {

    @Value("${jwt.access.secret}")
    private String accessSecret;

    @Value("${jwt.access.expirationTime}")
    private String expirationTime;

    private static final String ROLE = "role";
    private static final String EMAIL = "email";

    public String buildAccessJwt(User user) {
        final long expirationTimeLong = Long.parseLong(expirationTime); // в секундах
        final Date creationDate = new Date();
        final Date expirationDate = new Date(creationDate.getTime() + expirationTimeLong * 1_000);

        return Jwts.builder()
                .subject(String.valueOf(user.getId()))
                .claim(EMAIL, user.getEmail())
                .claim(ROLE, user.getRole())
                .issuedAt(creationDate)
                .expiration(expirationDate)
                .signWith(getSecretKey())
                .compact();
    }

    public boolean validateJwt(String jwt) {
        try {
            var claims = getJwtClaims(jwt);
            log.info("JWT expiration time: {}", claims.getExpiration());
            return true;
        } catch (ExpiredJwtException e) {
            log.error("JWT expired exception: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            log.error("Unsupported JWT exception: {}", e.getMessage());
        } catch (MalformedJwtException e) {
            log.error("Malformed JWT exception: {}", e.getMessage());
        } catch (Exception e) {
            log.error("Invalid JWT exception: {}", e.getMessage());
        }
        return false;
    }

    public CustomAuthentication getJwtAuthentication (Claims claims) {
        return CustomAuthentication
                .builder()
                .email(claims.get("email", String.class))
                .role(claims.get("role", String.class))
                .build();
    }

    public Claims getJwtClaims(String jwt) {
        return Jwts.parser()
                .verifyWith(getSecretKey())
                .build()
                .parseSignedClaims(jwt)
                .getPayload();
    }

    private SecretKey getSecretKey() {
        return Keys.hmacShaKeyFor(accessSecret.getBytes(StandardCharsets.UTF_8));
    }

}
