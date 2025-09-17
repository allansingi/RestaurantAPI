package pt.allanborges.restaurant.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import pt.allanborges.restaurant.model.enums.Role;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.util.Base64;
import java.util.Collection;
import java.util.Date;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class JwtService {

    private final SecretKey key;
    private final String issuer;
    private final long expirationMillis;

    public JwtService(
            @Value("${app.jwt.secret}") String secret,
            @Value("${app.jwt.issuer}") String issuer,
            @Value("${app.jwt.expiration-minutes}") long expirationMinutes) {
        this.key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(
                isBase64(secret) ? secret : Base64.getEncoder().encodeToString(secret.getBytes())));
        this.issuer = issuer;
        this.expirationMillis = expirationMinutes * 60_000;
    }

    public String generate(String username, Set<Role> roles) {
        Instant now = Instant.now();
        var roleNames = roles.stream().map(Role::name).toList();
        return Jwts.builder()
                .subject(username)
                .issuer(issuer)
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plusMillis(expirationMillis)))
                .claim("roles", roleNames)
                .signWith(key)
                .compact();
    }

    public String getUsername(String token) {
        return Jwts.parser().verifyWith(key).build()
                .parseSignedClaims(token).getPayload().getSubject();
    }

    public Set<String> getRoles(String token) {
        Object roles = Jwts.parser().verifyWith(key).build()
                .parseSignedClaims(token).getPayload().get("roles");
        if (roles instanceof Collection<?> collection) {
            return collection.stream().map(Object::toString).collect(Collectors.toSet());
        }
        return Set.of();
    }

    public boolean isValid(String token) {
        try {
            Jwts.parser().verifyWith(key).build().parseSignedClaims(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private static boolean isBase64(String s) {
        try {
            Decoders.BASE64.decode(s);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

}