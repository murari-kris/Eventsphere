package com.college.events.service;

import com.college.events.dto.ScoreSubmissionRequest;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.HashMap;
import java.util.Map;

@Service
public class ScoreService {

    private final SimpMessagingTemplate messagingTemplate;
    // Inject your score repository here when your DB maps are ready
    
    public ScoreService(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    @Transactional
    public Map<String, Object> submitAndBroadcastScore(ScoreSubmissionRequest request) {
        // Business logic execution wrapper
        Map<String, Object> broadcastPayload = new HashMap<>();
        broadcastPayload.put("eventId", request.getEventId());
        broadcastPayload.put("teamName", request.getTeamName());
        broadcastPayload.put("score", request.getScoreValue());
        broadcastPayload.put("timestamp", System.currentTimeMillis());

        // FEATURE 2: Broadcast metrics straight to public dashboards via WebSockets broker
        messagingTemplate.convertAndSend("/topic/leaderboard/" + request.getEventId(), broadcastPayload);

        return broadcastPayload;
    }
}