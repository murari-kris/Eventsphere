package com.college.events.controller;

import com.college.events.dto.LoginRequest;
import com.college.events.dto.RegisterRequest;
import com.college.events.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth") // Combined prefix route sequence matching http://localhost:8084/api/auth
@CrossOrigin(origins = "*")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody RegisterRequest request) {
        try {
            Map<String, Object> responseData = authService.register(request);
            return ResponseEntity.ok(responseData);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(400).body(Map.of("message", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("message", "An unexpected server error occurred."));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody LoginRequest request) {
        try {
            Map<String, Object> responseData = authService.login(request);
            return ResponseEntity.ok(responseData);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(400).body(Map.of("message", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("message", "An unexpected server error occurred."));
        }
    }
}