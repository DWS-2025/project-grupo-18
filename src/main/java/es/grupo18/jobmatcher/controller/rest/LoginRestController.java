package es.grupo18.jobmatcher.controller.rest;

import es.grupo18.jobmatcher.security.jwt.AuthResponse;
import es.grupo18.jobmatcher.security.jwt.LoginRequest;
import es.grupo18.jobmatcher.security.jwt.JWTTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/login")
public class LoginRestController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JWTTokenProvider jwtTokenProvider;

    @Autowired
    private org.springframework.security.core.userdetails.UserDetailsService userDetailsService;

    @PostMapping
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
            );
            if (authentication.isAuthenticated()) {
                UserDetails userDetails = userDetailsService.loadUserByUsername(request.getEmail());
                String accessToken = jwtTokenProvider.generateAccessToken(userDetails);
                String refreshToken = jwtTokenProvider.generateRefreshToken(userDetails);

                AuthResponse response = new AuthResponse(AuthResponse.Status.SUCCESS, "Login correcto");
                response.setAccessToken(accessToken);
                response.setRefreshToken(refreshToken);
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.status(401).body(new AuthResponse(AuthResponse.Status.FAILURE, "Credenciales incorrectas"));
            }
        } catch (Exception e) {
            return ResponseEntity.status(401).body(new AuthResponse(AuthResponse.Status.FAILURE, "Credenciales incorrectas", e.getMessage()));
        }
    }
}
