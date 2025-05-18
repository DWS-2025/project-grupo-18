package es.grupo18.jobmatcher.security.jwt;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Component;

@Component
public class RevokedTokenStore {

    private final Map<String, Instant> revokedTokens = new ConcurrentHashMap<>();

    public void revoke(String token, Instant expiration) {
        revokedTokens.put(token, expiration);
    }

    public boolean isRevoked(String token) {
        Instant exp = revokedTokens.get(token);
        if (exp == null) return false;
        return Instant.now().isBefore(exp);
    }

    public void cleanupExpired() {
        Instant now = Instant.now();
        revokedTokens.entrySet().removeIf(e -> now.isAfter(e.getValue()));
    }
    
}
