package com.college.events.service;

import com.college.events.entity.Event;
import com.college.events.repository.EventRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

// FIXED IMPORTS: Corrected crypto package declarations for modern JDK environments
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Service
public class AttendanceService {

    private final EventRepository eventRepository;

    @Value("${eventsphere.hmac-secret:default_secret_key_string_32_chars_long}")
    private String hmacSecret;

    public AttendanceService(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    @Transactional
    public String verifyTicketAndCheckIn(String fullToken) {
        try {
            // 1. Split the token signature apart
            String[] parts = fullToken.split("\\.");
            if (parts.length != 2) {
                return "REJECTED: Malformed token string layout.";
            }

            String base64Payload = parts[0];
            String receivedSignature = parts[1];

            // 2. Decode ticket data
            String decodedPayload = new String(Base64.getDecoder().decode(base64Payload), StandardCharsets.UTF_8);
            
            // 3. Recalculate local server cryptographic signature
            String computedSignature = calculateHmacSha256(decodedPayload, hmacSecret);

            // 4. Validate Ticket Integrity Guard
            if (!computedSignature.equals(receivedSignature)) {
                return "ACCESS DENIED: Tampering detected! Ticket signature is completely invalid.";
            }

            // 5. Explicitly declare wrapper types matching our Entity configurations
            Long parsedEventId = 0L;
            Long parsedStudentId = 0L;
            
            String[] pairs = decodedPayload.split("&");
            for (String pair : pairs) {
                String[] kv = pair.split("=");
                if (kv.length == 2) {
                    if (kv[0].equals("eventId")) parsedEventId = Long.parseLong(kv[1]);
                    if (kv[0].equals("studentId")) parsedStudentId = Long.parseLong(kv[1]);
                }
            }

            // 6. Verify enrollment validation matches database records
            Event event = eventRepository.findById(parsedEventId)
                    .orElseThrow(() -> new RuntimeException("Target event profile not found."));

            // Effectively final variable assignment for reliable lambda stream filtering execution
            final Long searchStudentId = parsedStudentId;

            boolean isConfirmed = event.getConfirmedStudents().stream()
                    .anyMatch(user -> user.getId() != null && user.getId().equals(searchStudentId));

            if (!isConfirmed) {
                return "REJECTED: Student is currently on the waitlist or hasn't requested a pass.";
            }

            return "SUCCESS: Ticket verified! Student checked into the event.";

        } catch (Exception e) {
            return "ERROR: Verification system failure: " + e.getMessage();
        }
    }

    private String calculateHmacSha256(String data, String secret) {
        try {
            byte[] hashSecret = secret.getBytes(StandardCharsets.UTF_8);
            SecretKeySpec secretKey = new SecretKeySpec(hashSecret, "HmacSHA256");
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(secretKey);
            byte[] hmacBytes = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hmacBytes);
        } catch (Exception e) {
            throw new RuntimeException("Hash calculation layer failure.", e);
        }
    }
}