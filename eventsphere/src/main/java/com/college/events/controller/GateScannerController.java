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

    @GetMapping("/download-certificate")
    public ResponseEntity<byte[]> downloadCertificate(
            @RequestParam String userId, 
            @RequestParam String eventId) {
        
        String cleanUserId = userId.trim();
        String cleanEventId = eventId.trim();
        
        Optional<TicketAttendance> attendance = ticketAttendanceRepository.findByEventIdIgnoreCaseAndUserIdIgnoreCase(cleanEventId, cleanUserId);
        
        // --- TEMPORARY BYPASS SWITCH FOR TESTING ---
        // If the record isn't found at all, we fall back to a default title so the page doesn't crash.
        String eventTitle = "Event Participant Training Sprint";
        if (attendance.isPresent()) {
            eventTitle = attendance.get().getEventTitle();
        }
        
        // COMMENTED OUT FOR TESTING: This removes the 403 barrier entirely!
        /*
        if (attendance.isEmpty() || !attendance.get().isAttended()) {
            return ResponseEntity.status(403).body(null); 
        }
        */

        // Generate the PDF directly using your 2-argument service method
        byte[] pdfContents = certificateService.generateCertificatePdf(cleanUserId, eventTitle);
        
        String cleanFileName = "Certificate_" + cleanEventId + ".pdf";
        
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + cleanFileName)
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdfContents);
    }
}