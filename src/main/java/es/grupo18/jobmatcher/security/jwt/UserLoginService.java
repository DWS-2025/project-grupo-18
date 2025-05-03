package es.grupo18.jobmatcher.security.jwt;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;

@Service
public class UserLoginService {

    private static final Logger log = LoggerFactory.getLogger(UserLoginService.class);

    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;
    private final JWTTokenProvider jwtTokenProvider;

    public UserLoginService(AuthenticationManager authenticationManager, UserDetailsService userDetailsService,
            JWTTokenProvider jwtTokenProvider) {
        this.authenticationManager = authenticationManager;
        this.userDetailsService = userDetailsService;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    public ResponseEntity<AuthResponse> login(HttpServletResponse response, LoginRequest loginRequest) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        String username = loginRequest.getEmail();
        UserDetails user = userDetailsService.loadUserByUsername(username);

        HttpHeaders responseHeaders = new HttpHeaders();
        var newAccessToken = jwtTokenProvider.generateAccessToken(user);
        var newRefreshToken = jwtTokenProvider.generateRefreshToken(user);

        response.addCookie(buildTokenCookie(TokenType.ACCESS, newAccessToken));
        response.addCookie(buildTokenCookie(TokenType.REFRESH, newRefreshToken));

        AuthResponse loginResponse = new AuthResponse(AuthResponse.Status.SUCCESS,
                "Auth successful. Tokens are created in cookie.");
        return ResponseEntity.ok().headers(responseHeaders).body(loginResponse);
    }

    public ResponseEntity<AuthResponse> refresh(HttpServletResponse response, String refreshToken) {
        try {
            var claims = jwtTokenProvider.validateToken(refreshToken);
            UserDetails user = userDetailsService.loadUserByUsername(claims.getSubject());

            var newAccessToken = jwtTokenProvider.generateAccessToken(user);
            response.addCookie(buildTokenCookie(TokenType.ACCESS, newAccessToken));

            AuthResponse loginResponse = new AuthResponse(AuthResponse.Status.SUCCESS,
                    "Auth successful. Tokens are created in cookie.");
            return ResponseEntity.ok().body(loginResponse);

        } catch (Exception e) {
            log.error("Error while processing refresh token", e);
            AuthResponse loginResponse = new AuthResponse(AuthResponse.Status.FAILURE,
                    "Failure while processing refresh token");
            return ResponseEntity.ok().body(loginResponse);
        }
    }

    public String logout(HttpServletResponse response) {
        SecurityContextHolder.clearContext();
        response.addCookie(removeTokenCookie(TokenType.ACCESS));
        response.addCookie(removeTokenCookie(TokenType.REFRESH));

        return "logout successfully";
    }

    private Cookie buildTokenCookie(TokenType type, String token) {
        Cookie cookie = new Cookie(type.cookieName, token);
        cookie.setMaxAge((int) type.duration.getSeconds());
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        return cookie;
    }

    private Cookie removeTokenCookie(TokenType type) {
        Cookie cookie = new Cookie(type.cookieName, "");
        cookie.setMaxAge(0);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        return cookie;
    }

}
