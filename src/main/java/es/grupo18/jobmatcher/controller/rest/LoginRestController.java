package es.grupo18.jobmatcher.controller.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import es.grupo18.jobmatcher.security.jwt.AuthResponse;
import es.grupo18.jobmatcher.security.jwt.JWTTokenProvider;
import es.grupo18.jobmatcher.security.jwt.LoginRequest;
import es.grupo18.jobmatcher.security.jwt.UserLoginService;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/api/login")
public class LoginRestController {

    @Autowired
    private UserLoginService userLoginService;

    @Autowired
    private JWTTokenProvider jwtTokenProvider;

    @PostMapping
    public ResponseEntity<AuthResponse> login(
            @CookieValue(name = "RefreshToken", required = false) String refreshToken,
            @RequestBody LoginRequest request,
            HttpServletResponse response,
            HttpServletRequest httpRequest) {

        Long blockedUntil = (Long) httpRequest.getSession().getAttribute("loginBlockedUntil");
        long now = System.currentTimeMillis();

        if (blockedUntil != null && now < blockedUntil) {
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                    .body(new AuthResponse(AuthResponse.Status.FAILURE,
                            "Too many failed attempts. Try again later."));
        }

        if (refreshToken != null && !refreshToken.isBlank()) {
            try {
                jwtTokenProvider.validateRefreshToken(refreshToken);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new AuthResponse(AuthResponse.Status.FAILURE,
                                "Session already active"));
            } catch (JwtException ex) {
                System.out.println("JWT exception");
            }
        }

        try {
            return userLoginService.login(response, request);
        } catch (Exception ex) {
            httpRequest.getSession().setAttribute("loginBlockedUntil", now + 10_000);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new AuthResponse(AuthResponse.Status.FAILURE,
                            "Invalid credentials"));
        }
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refresh(
            @CookieValue(name = "RefreshToken", required = false) String refreshToken,
            HttpServletResponse response) {

        if (refreshToken == null || refreshToken.isBlank()) {
            return ResponseEntity.badRequest()
                    .body(new AuthResponse(AuthResponse.Status.FAILURE,
                            "Empty refresh token cookie"));
        }

        try {
            return userLoginService.refresh(response, refreshToken);
        } catch (ExpiredJwtException ex) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new AuthResponse(AuthResponse.Status.FAILURE,
                            "Refresh token expired"));
        } catch (JwtException ex) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new AuthResponse(AuthResponse.Status.FAILURE,
                            "Invalid refresh token"));
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new AuthResponse(AuthResponse.Status.FAILURE,
                            "Error processing refresh token"));
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<AuthResponse> logout(
            @CookieValue(name = "RefreshToken", required = false) String refreshToken,
            HttpServletRequest request,
            HttpServletResponse response) {

        if (refreshToken == null || refreshToken.isBlank()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new AuthResponse(AuthResponse.Status.FAILURE,
                            "No user logged, cannot logout"));
        }

        userLoginService.logout(request, response);

        return ResponseEntity.ok(new AuthResponse(AuthResponse.Status.SUCCESS,
                "Session closed successfully"));
    }
}
