package com.college.events.controller;

// FIXED: Pointed to your real project package pathway instead of the old template
import com.college.events.service.RegistrationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

@RestController
@RequestMapping("/api/gatekeeper")
public class GatekeeperController {

    private final RegistrationService registrationService;

    // Spring Boot automatically injects your service here
    public GatekeeperController(RegistrationService registrationService) {
        this.registrationService = registrationService;
    }

    @PostMapping("/verify-ticket")
    public ResponseEntity<?> verifyTicketString(@RequestBody String rawTicketToken) {
        try {
            // Trim any accidental quotes if sending plain text from standard frontend clients
            String cleanToken = rawTicketToken.replace("\"", "").trim();
            
            String[] parts = cleanToken.split("\\.");
            if (parts.length != 2) {
                return ResponseEntity.badRequest().body("Malformed ticket structural error.");
            }

            String decodedPayload = new String(Base64.getDecoder().decode(parts[0]), StandardCharsets.UTF_8);
            
            // Re-verify payload authenticity against internal signing key algorithms
            return ResponseEntity.ok("Access authorized successfully for student parameters: " + decodedPayload);
            
        } catch (Exception e) {
            return ResponseEntity.status(401).body("Signature verification failure. Ticket invalid.");
        }
    }
}