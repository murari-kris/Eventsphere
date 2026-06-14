package com.college.events.controller;

// Both of these imports will now compile completely clean!
import com.college.events.dto.ScoreSubmissionRequest;
import com.college.events.service.ScoreService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/scores")
@CrossOrigin(origins = "*")
public class ScoreController {

    private final ScoreService scoreService;

    // Spring Boot automatically hooks up and injects the service mapping bean layer here
    public ScoreController(ScoreService scoreService) {
        this.scoreService = scoreService;
    }

    @PostMapping("/submit")
    public ResponseEntity<?> submitScore(@RequestBody ScoreSubmissionRequest request) {
        try {
            Map<String, Object> result = scoreService.submitAndBroadcastScore(request);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Failed to process scoring payload: " + e.getMessage());
        }
    }
}