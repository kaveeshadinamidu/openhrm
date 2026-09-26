package com.openhrm.auth;

import com.openhrm.auth.dto.AuthResponse;
import com.openhrm.auth.dto.LoginRequest;
import com.openhrm.auth.dto.RegisterOrganizationRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register-organization")
    public ResponseEntity<AuthResponse> registerOrganization(@Valid @RequestBody RegisterOrganizationRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.registerOrganization(request));
    }

    @PostMapping("/login")
    public AuthResponse login(@Valid @RequestBody LoginRequest request) {
        return authService.login(request);
    }
}
