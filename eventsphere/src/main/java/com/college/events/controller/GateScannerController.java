package com.college.events.controller;

import com.college.events.entity.TicketAttendance;
import com.college.events.repository.TicketAttendanceRepository;
import com.college.events.service.CertificateService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/tickets")
@CrossOrigin(origins = "*") 
public class GateScannerController {

    private final TicketAttendanceRepository ticketAttendanceRepository;
    private final CertificateService certificateService;

    public GateScannerController(TicketAttendanceRepository ticketAttendanceRepository, CertificateService certificateService) {
        this.ticketAttendanceRepository = ticketAttendanceRepository;
        this.certificateService = certificateService;
    }

    // 1. ENDPOINT TO DOWNLOAD THE CERTIFICATE
    @GetMapping("/download-certificate")
    public ResponseEntity<byte[]> downloadCertificate(
            @RequestParam String userId, 
            @RequestParam String eventId) {
        
        String cleanUserId = userId.trim();
        String cleanEventId = eventId.trim();
        
        List<TicketAttendance> attendanceRecords = ticketAttendanceRepository.findAllByEventIdIgnoreCaseAndUserIdIgnoreCase(cleanEventId, cleanUserId);
        
        if (attendanceRecords.isEmpty()) {
            return ResponseEntity.status(403).body(null); 
        }

        boolean hasAttended = attendanceRecords.stream().anyMatch(TicketAttendance::isAttended);
        
        if (!hasAttended) {
            return ResponseEntity.status(403).body(null); 
        }

        String eventTitle = attendanceRecords.stream()
                .map(TicketAttendance::getEventTitle)
                .filter(title -> title != null && !title.isEmpty())
                .findFirst()
                .orElse("Event Participant Training Sprint");

        byte[] pdfContents = certificateService.generateCertificatePdf(cleanUserId, eventTitle);
        
        String cleanFileName = "Certificate_" + cleanEventId + ".pdf";
        
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + cleanFileName)
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdfContents);
    }

    // 2. ENDPOINT FOR THE QR CODE SCANNER
    @PostMapping("/verify-scan")
    public ResponseEntity<String> verifyScan(@RequestParam String userId, @RequestParam String eventId) {
        String cleanUserId = userId.trim();
        String cleanEventId = eventId.trim();
        
        List<TicketAttendance> attendanceRecords = ticketAttendanceRepository.findAllByEventIdIgnoreCaseAndUserIdIgnoreCase(cleanEventId, cleanUserId);
        
        if (!attendanceRecords.isEmpty()) {
            for (TicketAttendance attendance : attendanceRecords) {
                attendance.setAttended(true);
                ticketAttendanceRepository.save(attendance);
            }
            return ResponseEntity.ok("Attendance recorded successfully! Certificate unlocked.");
        } else {
            TicketAttendance fallbackAttendance = new TicketAttendance(
                null, 
                cleanUserId, 
                cleanEventId, 
                "Event Tech Summit Sprint", 
                true
            );
                    
            ticketAttendanceRepository.save(fallbackAttendance);
            return ResponseEntity.ok("No prior ledger found, but new verified record created successfully! Certificate unlocked.");
        }
    }
}