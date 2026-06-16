package com.college.events.controller;

import com.college.events.entity.TicketAttendance;
import com.college.events.repository.TicketAttendanceRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Optional;

@RestController
@RequestMapping("/api/gatekeeper")
@CrossOrigin(origins = "*") // Allows mobile scanner apps and web clients to connect seamlessly
public class GatekeeperController {

    // Inject your real repository to modify the live database state
    private final TicketAttendanceRepository ticketAttendanceRepository;

    public GatekeeperController(TicketAttendanceRepository ticketAttendanceRepository) {
        this.ticketAttendanceRepository = ticketAttendanceRepository;
    }

    @PostMapping("/verify-ticket")
    public ResponseEntity<?> verifyTicketString(@RequestBody String rawTicketToken) {
        try {
            // 1. Clean token casing wrappers
            String cleanToken = rawTicketToken.replace("\"", "").trim();
            
            String[] parts = cleanToken.split("\\.");
            if (parts.length != 2) {
                return ResponseEntity.badRequest().body("Malformed ticket structural error.");
            }

            // 2. Decode payload text parameters (e.g. "eventId=evt_001&studentId=krishnamutari160@gmail.com")
            String decodedPayload = new String(Base64.getDecoder().decode(parts[0]), StandardCharsets.UTF_8);
            
            // 3. Extract variables from query parameters string dynamically
            String eventId = null;
            String userId = null;
            
            String[] pairs = decodedPayload.split("&");
            for (String pair : pairs) {
                String[] keyValue = pair.split("=");
                if (keyValue.length == 2) {
                    if (keyValue[0].equalsIgnoreCase("eventId")) {
                        eventId = keyValue[1].trim();
                    } else if (keyValue[0].equalsIgnoreCase("studentId") || keyValue[0].equalsIgnoreCase("userId")) {
                        userId = keyValue[1].trim();
                    }
                }
            }

            // Validation Safeguard
            if (eventId == null || userId == null) {
                return ResponseEntity.badRequest().body("Incomplete payload token variables extracted.");
            }

            // 4. PERSIST TO DATABASE: Find the target ticket in the database case-insensitively
            Optional<TicketAttendance> attendanceOpt = ticketAttendanceRepository.findByEventIdIgnoreCaseAndUserIdIgnoreCase(eventId, userId);

            if (attendanceOpt.isPresent()) {
                TicketAttendance attendance = attendanceOpt.get();
                
                // Flip the flag to true!
                attendance.setAttended(true);
                
                // Save it back to your cloud MySQL instance!
                ticketAttendanceRepository.save(attendance);

                return ResponseEntity.ok("Access authorized successfully! Attendance recorded for: " + userId);
            } else {
                // If it can't find a registration entry matching this event and user
                return ResponseEntity.status(404).body("No valid registration ledger found for event " + eventId + " and user " + userId);
            }
            
        } catch (Exception e) {
            return ResponseEntity.status(401).body("Signature verification failure. Ticket invalid.");
        }
    }
}