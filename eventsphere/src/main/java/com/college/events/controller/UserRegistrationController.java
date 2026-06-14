package com.college.events.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;

@RestController
@RequestMapping("/api/registrations")
@CrossOrigin(origins = "*")
public class UserRegistrationController {

    @GetMapping("/my")
    public ResponseEntity<?> getMyRegistrations(@RequestHeader(value = "Authorization", required = false) String token) {
        // The frontend passes 'Bearer jwt-token' in headers. 
        // For now, let's return a clean list of events this student is attending.
        List<Map<String, Object>> myRegisteredEvents = new ArrayList<>();
        
        Map<String, Object> joinedEvent = Map.of(
            "id", 1L,
            "title", "HackWithInfy Practice Hackathon",
            "category", "HACKATHON",
            "venue", "Lab 3, CSE Block",
            "startDateTime", "2026-06-15T10:00:00",
            "totalSeats", 120,
            "registeredCount", 95,
            "teamEvent", true,
            "maxTeamSize", 4,
            "leaderboardEnabled", true
        );
        
        myRegisteredEvents.add(joinedEvent);
        
        return ResponseEntity.ok(myRegisteredEvents);
    }
}