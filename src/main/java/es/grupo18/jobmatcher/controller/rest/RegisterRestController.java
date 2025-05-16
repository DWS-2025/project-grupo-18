package es.grupo18.jobmatcher.controller.rest;

import es.grupo18.jobmatcher.security.jwt.AuthResponse;
import es.grupo18.jobmatcher.security.jwt.RegisterRequest;
import es.grupo18.jobmatcher.security.jwt.UserLoginService;
import jakarta.validation.Valid;

import org.owasp.html.PolicyFactory;
import org.owasp.html.Sanitizers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/register")
public class RegisterRestController {

    @Autowired
    private UserLoginService userLoginService;


    private static final PolicyFactory TEXT_SANITIZER = Sanitizers.FORMATTING;

    @PostMapping
    public ResponseEntity<AuthResponse> registerUser(@Valid @RequestBody RegisterRequest request) {
        request.setName(TEXT_SANITIZER.sanitize(request.getName()));
        AuthResponse response = userLoginService.register(request);
        return ResponseEntity.ok(response);
    }
}
