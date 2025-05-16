package es.grupo18.jobmatcher.controller.rest;

import es.grupo18.jobmatcher.security.jwt.AuthResponse;
import es.grupo18.jobmatcher.security.jwt.RegisterRequest;
import es.grupo18.jobmatcher.security.jwt.UserLoginService;
import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/register")
public class RegisterRestController {

    @Autowired
    private UserLoginService userLoginService;

    @PostMapping
    public ResponseEntity<AuthResponse> registerUser(@Valid @RequestBody RegisterRequest request) {
        AuthResponse response = userLoginService.register(request);
        return ResponseEntity.ok(response);
    }

}
