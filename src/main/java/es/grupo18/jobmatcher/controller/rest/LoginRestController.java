package es.grupo18.jobmatcher.controller.rest;

import es.grupo18.jobmatcher.security.jwt.AuthResponse;
import es.grupo18.jobmatcher.security.jwt.LoginRequest;
import es.grupo18.jobmatcher.security.jwt.UserLoginService;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/login")
public class LoginRestController {

    @Autowired
    private UserLoginService userLoginService;

    @PostMapping
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request, HttpServletResponse response) {
        return userLoginService.login(response, request);
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refresh(
            @CookieValue(name = "RefreshToken", required = false) String refreshToken,
            HttpServletResponse response) {
        if (refreshToken == null || refreshToken.isBlank()) {
            return ResponseEntity
                    .badRequest()
                    .body(new AuthResponse(AuthResponse.Status.FAILURE, "Empty refresh token cookie"));
        }
        try {
            return userLoginService.refresh(response, refreshToken);

        } catch (ExpiredJwtException ex) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(new AuthResponse(AuthResponse.Status.FAILURE, "Refresh token outdated"));

        } catch (JwtException ex) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(new AuthResponse(AuthResponse.Status.FAILURE, "Invalid refresh token"));

        } catch (Exception ex) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new AuthResponse(AuthResponse.Status.FAILURE, "Internal error while refreshing token"));
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<AuthResponse> logout(
            @CookieValue(name = "RefreshToken", required = false) String refreshToken,
            HttpServletResponse response) {
        if (refreshToken == null || refreshToken.isBlank()) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new AuthResponse(AuthResponse.Status.FAILURE,
                            "No user logged, cannot logout"));
        }
        userLoginService.logout(response);
        return ResponseEntity.ok(new AuthResponse(AuthResponse.Status.SUCCESS,
                "Session closed successfully"));
    }

}
