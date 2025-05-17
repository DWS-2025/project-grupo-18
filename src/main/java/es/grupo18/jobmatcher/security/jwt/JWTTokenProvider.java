package es.grupo18.jobmatcher.security.jwt;

import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import es.grupo18.jobmatcher.model.User;
import es.grupo18.jobmatcher.service.UserService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import io.jsonwebtoken.JwtException;

@Component
public class JWTTokenProvider {

    @Autowired
    private UserService userService;

    // HS256 generated secret key
    private final SecretKey jwtSecret = Jwts.SIG.HS256.key().build();

    // Parser that will be used to validate the JWT
    private final JwtParser jwtParser = Jwts.parser().verifyWith(jwtSecret).build();

    public String tokenStringFromHeaders(HttpServletRequest req) {
        String bearerToken = req.getHeader(HttpHeaders.AUTHORIZATION);
        if (bearerToken == null || bearerToken.isBlank()) {
            throw new IllegalArgumentException("Missing Authorization header");
        }
        if (!bearerToken.startsWith("Bearer ")) {
            throw new IllegalArgumentException("Authorization header must start with Bearer");
        }
        return bearerToken.substring(7);
    }

    public String tokenStringFromCookies(HttpServletRequest request) {
        var cookies = request.getCookies();
        if (cookies == null) {
            throw new IllegalArgumentException("No cookies found in request");
        }

        for (Cookie cookie : cookies) {
            if (TokenType.ACCESS.cookieName.equals(cookie.getName())) {
                String accessToken = cookie.getValue();
                if (accessToken == null || accessToken.isBlank()) {
                    throw new IllegalArgumentException("Access token cookie is empty");
                }
                return accessToken;
            }
        }
        throw new IllegalArgumentException("No access token cookie found in request");
    }

    public Claims validateToken(HttpServletRequest req, boolean fromCookie) {
        var token = fromCookie ? tokenStringFromCookies(req) : tokenStringFromHeaders(req);
        return validateToken(token);
    }

    public Claims validateToken(String token) {
        return jwtParser.parseSignedClaims(token).getPayload();
    }

    public String generateAccessToken(UserDetails userDetails) {
        return buildToken(TokenType.ACCESS, userDetails).compact();
    }

    public String generateRefreshToken(UserDetails userDetails) {
        return buildToken(TokenType.REFRESH, userDetails).compact();
    }

    private JwtBuilder buildToken(TokenType tokenType, UserDetails userDetails) {
        var now = new Date();
        var expiry = Date.from(now.toInstant().plus(tokenType.duration));

        User user = userService.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        return Jwts.builder()
                .claim("roles", userDetails.getAuthorities())
                .claim("type", tokenType.name())
                .claim("userId", user.getId())
                .subject(userDetails.getUsername())
                .issuedAt(now)
                .expiration(expiry)
                .signWith(jwtSecret);
    }

    public Long getUserIdFromToken(String token) {
        if (token == null || token.trim().isEmpty()) {
            throw new IllegalArgumentException("JWT token cannot be null or empty");
        }

        Claims claims = jwtParser.parseSignedClaims(token).getPayload();

        Object userId = claims.get("userId");
        if (userId == null) {
            throw new IllegalArgumentException("Token does not contain 'userId' field");
        }

        return Long.parseLong(userId.toString());
    }

    public void validateRefreshToken(String token) {
        if (token == null || token.isBlank()) {
            throw new IllegalArgumentException("Refresh token cannot be null or empty");
        }

        Claims claims = validateToken(token);
        String type = claims.get("type", String.class);
        if (!TokenType.REFRESH.name().equals(type)) {
            throw new JwtException("Token is not a refresh token");
        }
    }

}
