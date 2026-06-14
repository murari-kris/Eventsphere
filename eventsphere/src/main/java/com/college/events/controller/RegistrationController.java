package com.college.events.controller;

import com.college.events.service.RegistrationService;
import com.college.events.service.RegistrationResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/events")
@CrossOrigin(origins = "*")
public class RegistrationController {

    private final RegistrationService registrationService;

    public RegistrationController(RegistrationService registrationService) {
        this.registrationService = registrationService;
    }

    /**
     * GET /api/events/public?size=50
     * Fetches public events to display on the student dashboard cards.
     */
    @GetMapping("/public")
    public ResponseEntity<?> getPublicEvents(@RequestParam(defaultValue = "50") int size) {
        // Mock data structure tailored to match your frontend eventCard() renderer properties perfectly
        List<Map<String, Object>> mockEvents = new ArrayList<>();
        
        mockEvents.add(Map.of(
            "id", 1L,
            "title", "HackWithInfy Mock Hackathon",
            "category", "HACKATHON",
            "venue", "Lab 3, CSE Block",
            "startDateTime", "2026-06-15T10:00:00",
            "totalSeats", 120,
            "registeredCount", 95,
            "teamEvent", true,
            "maxTeamSize", 4,
            "leaderboardEnabled", true
        ));
        
        mockEvents.add(Map.of(
            "id", 2L,
            "title", "Full-Stack Web Development BootCamp",
            "category", "WORKSHOP",
            "venue", "Main Seminar Hall",
            "startDateTime", "2026-06-18T14:00:00",
            "totalSeats", 60,
            "registeredCount", 58,
            "teamEvent", false,
            "maxTeamSize", 1,
            "leaderboardEnabled", false
        ));

        // Safely slice the list according to the request size query parameter limit
        int limit = Math.min(size, mockEvents.size());
        return ResponseEntity.ok(mockEvents.subList(0, limit));
    }

    @PostMapping("/{eventId}/register/{studentId}")
    public ResponseEntity<RegistrationResponse> registerStudent(
            @PathVariable Long eventId, 
            @PathVariable Long studentId) {
        RegistrationResponse response = registrationService.registerStudent(eventId, studentId);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{eventId}/cancel/{studentId}")
    public ResponseEntity<?> cancelRegistration(
            @PathVariable Long eventId, 
            @PathVariable Long studentId) {
        registrationService.cancelRegistration(eventId, studentId);
        return ResponseEntity.ok("Cancellation processed successfully.");
    }
}