package com.college.events.controller;

import com.college.events.dto.ScanRequest;
import com.college.events.entity.TicketAttendance;
import com.college.events.repository.TicketAttendanceRepository;
import com.college.events.service.CertificateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/tickets")
@CrossOrigin(origins = "*")
public class GateScannerController {

    @Autowired
    private CertificateService certificateService;

    @Autowired
    private TicketAttendanceRepository attendanceRepository;

    /**
     * 📸 GATE SCANNER VERIFICATION ROUTE
     * Marks attendance inside MySQL upon scanning a valid ticket pass.
     */
    @PostMapping("/verify")
    public ResponseEntity<Map<String, Object>> verifyTicket(@RequestBody ScanRequest request) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            String rawToken = request.getToken().trim().replace(" ", "+");

            byte[] decodedBytes;
            try {
                decodedBytes = Base64.getUrlDecoder().decode(rawToken);
            } catch (IllegalArgumentException e) {
                decodedBytes = Base64.getDecoder().decode(rawToken);
            }
            
            String decodedPayload = new String(decodedBytes, StandardCharsets.UTF_8);
            Map<String, String> params = parseTokenPayload(decodedPayload);
            
            String eventId = params.get("eventId");
            String userId = params.get("userId");
            
            if (eventId == null || userId == null) {
                response.put("status", "REJECTED");
                response.put("message", "Invalid Code Structure Signature.");
                return ResponseEntity.badRequest().body(response);
            }

            // 🛠️ FIX: Full matrix mapping matching your dashboard.html catalog catalogEvents
            String eventTitle;
            if ("evt_001".equalsIgnoreCase(eventId)) {
                eventTitle = "ByteCraft 2026: 24-Hour Hackathon";
            } else if ("evt_002".equalsIgnoreCase(eventId)) {
                eventTitle = "Generative AI & Chatbot Bootcamp";
            } else if ("evt_003".equalsIgnoreCase(eventId)) {
                eventTitle = "Symphony '26: Annual Talent Fest";
            } else if ("evt_004".equalsIgnoreCase(eventId)) {
                eventTitle = "UI/UX Masterclass: Designing Apps";
            } else if ("evt_005".equalsIgnoreCase(eventId)) {
                eventTitle = "Campus Gaming Arena Tournament";
            } else if ("evt_006".equalsIgnoreCase(eventId)) {
                eventTitle = "Startup Pitch Fest '26: Final Call";
            } else {
                eventTitle = "Advanced Technology Seminar Series";
            }

            // ⚡ OPTION B LOGIC: Query Database for Double Entry and Record Check-In
            Optional<TicketAttendance> existingRecord = attendanceRepository.findByEventIdAndUserId(eventId, userId);
            
            if (existingRecord.isPresent() && existingRecord.get().isAttended()) {
                response.put("status", "DUPLICATE");
                response.put("student", userId);
                response.put("message", "Access Denied: Ticket has already been scanned at the gate!");
                return ResponseEntity.ok(response);
            }

            // Using Manual Builder pattern layout to safely bypass IDE bytecode sync locks
            TicketAttendance attendance;
            if (existingRecord.isPresent()) {
                attendance = TicketAttendance.builder()
                        .id(existingRecord.get().getId())
                        .userId(userId)
                        .eventId(eventId)
                        .eventTitle(eventTitle)
                        .attended(true)
                        .build();
            } else {
                attendance = TicketAttendance.builder()
                        .userId(userId)
                        .eventId(eventId)
                        .eventTitle(eventTitle)
                        .attended(true)
                        .build();
            }
            
            attendanceRepository.save(attendance);
            System.out.println("💾 DATABASE UPDATED: " + userId + " checked into " + eventId + " (" + eventTitle + ")");

            response.put("status", "GRANTED");
            response.put("student", userId);
            response.put("event", eventId);
            response.put("message", "Access Granted! Attendance verified. Certificate unlocked on your dashboard.");
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("status", "ERROR");
            response.put("message", "System handling exception: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * 🎓 DYNAMIC DASHBOARD CERTIFICATE DOWNLOAD ROUTE
     * Verifies attendance in MySQL before allowing the document generation engine to run.
     */
    @GetMapping("/download-certificate")
    public ResponseEntity<byte[]> downloadCertificate(
            @RequestParam String userId, 
            @RequestParam String eventId) {
        
        // Ensure the student actually checked into this event at the gate first
        Optional<TicketAttendance> attendance = attendanceRepository.findByEventIdAndUserId(eventId, userId);
        
        if (attendance.isEmpty() || !attendance.get().isAttended()) {
            return ResponseEntity.status(403).body(null); // Forbidden if not verified at gate
        }

        // Run the generation tool engine using real data from our database
        byte[] pdfContents = certificateService.generateCertificatePdf(userId, attendance.get().getEventTitle());
        
        String cleanFileName = "Certificate_" + eventId + ".pdf";
        
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + cleanFileName)
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdfContents);
    }

    private Map<String, String> parseTokenPayload(String payload) {
        Map<String, String> map = new HashMap<>();
        String[] pairs = payload.split("&");
        for (String pair : pairs) {
            String[] kv = pair.split("=");
            if (kv.length == 2) {
                map.put(kv[0], kv[1]);
            }
        }
        return map;
    }
}