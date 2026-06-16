package com.college.events.controller;

import com.college.events.entity.TicketAttendance;
import com.college.events.repository.TicketAttendanceRepository;
import com.college.events.service.CertificateService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    // 1. ENDPOINT TO DOWNLOAD THE CERTIFICATE (Blocks if not scanned)
    @GetMapping("/download-certificate")
    public ResponseEntity<byte[]> downloadCertificate(
            @RequestParam String userId, 
            @RequestParam String eventId) {
        
        String cleanUserId = userId.trim();
        String cleanEventId = eventId.trim();
        
        Optional<TicketAttendance> attendance = ticketAttendanceRepository.findByEventIdIgnoreCaseAndUserIdIgnoreCase(cleanEventId, cleanUserId);
        
        // SECURITY LAYER: If the record is missing or attended is false, block with 403!
        if (attendance.isEmpty() || !attendance.get().isAttended()) {
            return ResponseEntity.status(403).body(null); 
        }

        String eventTitle = attendance.get().getEventTitle();
        byte[] pdfContents = certificateService.generateCertificatePdf(cleanUserId, eventTitle);
        
        String cleanFileName = "Certificate_" + cleanEventId + ".pdf";
        
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + cleanFileName)
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdfContents);
    }

    // 2. ENDPOINT FOR THE QR CODE SCANNER (Changes attended from false to true)
    @PostMapping("/verify-scan")
    public ResponseEntity<String> verifyScan(@RequestParam String userId, @RequestParam String eventId) {
        String cleanUserId = userId.trim();
        String cleanEventId = eventId.trim();
        
        Optional<TicketAttendance> attendanceOpt = ticketAttendanceRepository.findByEventIdIgnoreCaseAndUserIdIgnoreCase(cleanEventId, cleanUserId);
        
        if (attendanceOpt.isPresent()) {
            TicketAttendance attendance = attendanceOpt.get();
            
            // Flip attendance flag to true
            attendance.setAttended(true); 
            
            // Save the change directly into your MySQL database
            ticketAttendanceRepository.save(attendance); 
            
            return ResponseEntity.ok("Attendance recorded successfully! Certificate unlocked.");
        }
        
        return ResponseEntity.status(404).body("Registration record not found.");
    }
}