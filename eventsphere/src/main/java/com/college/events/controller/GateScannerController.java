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
        
        String cleanUserId = userId.trim().toLowerCase();
        String cleanEventId = eventId.trim().toLowerCase();
        
        Optional<TicketAttendance> attendance = ticketAttendanceRepository.findByEventIdAndUserId(cleanEventId, cleanUserId);
        
        if (attendance.isEmpty() || !attendance.get().isAttended()) {
            return ResponseEntity.status(403).body(null); 
        }

        String eventTitle = attendance.get().getEventTitle();

        // PASSING EXACTLY TWO ARGUMENTS HIERARCHY
        byte[] pdfContents = certificateService.generateCertificatePdf(cleanUserId, eventTitle);
        
        String cleanFileName = "Certificate_" + cleanEventId + ".pdf";
        
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + cleanFileName)
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdfContents);
    }
}