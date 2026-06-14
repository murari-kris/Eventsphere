package com.college.events.controller;

import com.college.events.service.AttendanceService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

@RestController
@RequestMapping("/api/test-qr")
@CrossOrigin(origins = "*")
public class QrTestController {

    private final AttendanceService attendanceService;

    @Value("${eventsphere.hmac-secret:default_secret_key_string_32_chars_long}")
    private String hmacSecret;

    public QrTestController(AttendanceService attendanceService) {
        this.attendanceService = attendanceService;
    }

    /**
     * TEST 1: Simulates the server generating a valid, signed ticket string.
     * URL to visit: http://localhost:8080/api/test-qr/create-ticket?eventId=1&studentId=1
     */
    @GetMapping("/create-ticket")
    public String createTestTicket(@RequestParam String eventId, @RequestParam String studentId) {
        try {
            // 1. Build the payload text
            String payload = "eventId=" + eventId + "&studentId=" + studentId;
            
            // 2. Convert to safe Base64 format
            String base64Payload = Base64.getEncoder().encodeToString(payload.getBytes(StandardCharsets.UTF_8));
            
            // 3. Generate the secret HMAC signature hash
            byte[] hashSecret = hmacSecret.getBytes(StandardCharsets.UTF_8);
            SecretKeySpec secretKey = new SecretKeySpec(hashSecret, "HmacSHA256");
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(secretKey);
            byte[] hmacBytes = mac.doFinal(payload.getBytes(StandardCharsets.UTF_8));
            String signature = Base64.getEncoder().encodeToString(hmacBytes);
            
            // 4. Glue them together with a dot (.)
            return base64Payload + "." + signature;
            
        } catch (Exception e) {
            return "Generation failed: " + e.getMessage();
        }
    }

    /**
     * TEST 2: Simulates a gate volunteer scanning the ticket token.
     * URL to visit: http://localhost:8080/api/test-qr/scan?token=PASTE_YOUR_TOKEN_HERE
     */
    @GetMapping("/scan")
    public String scanTestTicket(@RequestParam String token) {
        // This passes the scanned token directly into your live attendance matching logic!
        return attendanceService.verifyTicketAndCheckIn(token);
    }
}