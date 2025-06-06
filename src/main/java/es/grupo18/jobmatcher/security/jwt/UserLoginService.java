package es.grupo18.jobmatcher.security.jwt;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import es.grupo18.jobmatcher.exception.EmailAlreadyExistsException;
import es.grupo18.jobmatcher.exception.WeakPasswordException;
import es.grupo18.jobmatcher.model.User;
import es.grupo18.jobmatcher.repository.UserRepository;
import es.grupo18.jobmatcher.security.PasswordConstraintValidator;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@Service
public class UserLoginService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

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

    public AuthResponse register(RegisterRequest request) {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new EmailAlreadyExistsException(request.getEmail());
        }

        if (!PasswordConstraintValidator.isValid(request.getPassword())) {
            throw new WeakPasswordException(
                    "Password must be at least 8 characters long, contain at least one uppercase letter, one lowercase letter, one digit, and one special character.");
        }

        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setEncoded_password(passwordEncoder.encode(request.getPassword()));
        user.setRoles(List.of("USER"));

        userRepository.save(user);

        var userDetails = userDetailsService.loadUserByUsername(user.getEmail());

        String token = jwtTokenProvider.generateAccessToken(userDetails);
        return new AuthResponse(AuthResponse.Status.SUCCESS, "User registered successfully.");
    }

    public void registerAndAuthenticate(RegisterRequest req) {
        if (userRepository.findByEmail(req.getEmail()).isPresent()) {
            throw new RuntimeException("Email already in use");
        }
        if (!PasswordConstraintValidator.isValid(req.getPassword())) {
            throw new WeakPasswordException(
                    "Password must be at least 8 characters long, contain at least one uppercase letter, one lowercase letter, one digit, and one special character.");
        }
        User user = new User();
        user.setName(req.getName());
        user.setEmail(req.getEmail());
        user.setEncoded_password(passwordEncoder.encode(req.getPassword()));
        user.setRoles(List.of("USER"));
        userRepository.save(user);
        UserDetails userDetails = userDetailsService.loadUserByUsername(req.getEmail());
        Authentication authToken = new org.springframework.security.authentication.UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authToken);
    }

    public AuthResponse registerWithoutLogin(RegisterRequest request) {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            return new AuthResponse(AuthResponse.Status.FAILURE, "Email already in use");
        }
        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setEncoded_password(passwordEncoder.encode(request.getPassword()));
        user.setRoles(List.of("USER"));
        userRepository.save(user);
        return new AuthResponse(AuthResponse.Status.SUCCESS, "User registered successfully.");
    }

    public ResponseEntity<AuthResponse> refresh(HttpServletResponse response, String refreshToken) {
        try {
            Claims claims = jwtTokenProvider.validateToken(refreshToken);

            jwtTokenProvider.validateRefreshToken(refreshToken);

            jwtTokenProvider.revokeToken(refreshToken);

            UserDetails user = userDetailsService.loadUserByUsername(claims.getSubject());

            String newAccessToken = jwtTokenProvider.generateAccessToken(user);
            String newRefreshToken = jwtTokenProvider.generateRefreshToken(user);

            response.addCookie(buildTokenCookie(TokenType.ACCESS, newAccessToken));
            response.addCookie(buildTokenCookie(TokenType.REFRESH, newRefreshToken));

            AuthResponse loginResponse = new AuthResponse(
                    AuthResponse.Status.SUCCESS,
                    "Auth successful. Tokens renewed and old refresh token revoked.");
            return ResponseEntity.ok(loginResponse);

        } catch (Exception e) {
            log.error("Error while processing refresh token", e);
            AuthResponse loginResponse = new AuthResponse(
                    AuthResponse.Status.FAILURE,
                    "Error while processing refresh token");
            return ResponseEntity.ok(loginResponse);
        }
    }

    public String logout(HttpServletRequest request, HttpServletResponse response) {
        String token = jwtTokenProvider.tokenStringFromCookies(request);
        if (token != null) {
            jwtTokenProvider.revokeToken(token);
        }
        SecurityContextHolder.clearContext();
        response.addCookie(removeTokenCookie(TokenType.ACCESS));
        response.addCookie(removeTokenCookie(TokenType.REFRESH));

        HttpSession session = request.getSession(false);
        if (session != null)
            session.invalidate();
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
