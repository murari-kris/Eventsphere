package com.college.events.service;

import com.college.events.entity.*;
import com.college.events.repository.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

@Service
public class RegistrationService {

    private final EventRepository eventRepository;
    private final UserRepository userRepository;

    @Value("${eventsphere.hmac-secret:default_secret_key_string_32_chars_long}")
    private String hmacSecret;

    public RegistrationService(EventRepository eventRepository, UserRepository userRepository) {
        this.eventRepository = eventRepository;
        this.userRepository = userRepository;
    }

    /**
     * FEATURE 1 & 4: Secure Cryptographic Ticket Registration Lifecycle
     */
    @Transactional
    public RegistrationResponse registerStudent(Long eventId, Long studentId) {
        Event event = eventRepository.findById(eventId)
            .orElseThrow(() -> new RuntimeException("Target campus event record missing."));
        User student = userRepository.findById(studentId)
            .orElseThrow(() -> new RuntimeException("Student user account missing."));

        // Defensive step: Grab local list pointers safely
        List<User> confirmed = event.getConfirmedStudents();
        List<User> waitlisted = event.getWaitlistedStudents();

        // Deduplicate registration checks
        if (confirmed.contains(student) || waitlisted.contains(student)) {
            throw new IllegalArgumentException("User already holds a registered queue pointer token.");
        }

        // Allocate seats based on real-time capacity availability
        if (confirmed.size() < event.getMaxSeats()) {
            confirmed.add(student);
            eventRepository.save(event);

            // FEATURE 1: Generate secure HMAC-signed base64 verification ticket string
            String rawPayload = "eventId=" + eventId + "&studentId=" + studentId;
            String signature = calculateHmacSha256(rawPayload, hmacSecret);
            String rawToken = Base64.getEncoder().encodeToString(rawPayload.getBytes(StandardCharsets.UTF_8)) + "." + signature;

            return new RegistrationResponse("CONFIRMED", "Seat allocated successfully.", rawToken);
        } else {
            // Drop directly into the FIFO waitlist queue structure
            waitlisted.add(student);
            eventRepository.save(event);
            return new RegistrationResponse("WAITLISTED", "Event full. Added to waitlist queue.", null);
        }
    }

    /**
     * FEATURE 4: Secure Ticket Cancellation & Automatic Waitlist Queue Promotion Engine
     */
    @Transactional
    public void cancelRegistration(Long eventId, Long studentId) {
        Event event = eventRepository.findById(eventId)
            .orElseThrow(() -> new RuntimeException("Event not found."));
        User student = userRepository.findById(studentId)
            .orElseThrow(() -> new RuntimeException("User not found."));

        List<User> confirmed = event.getConfirmedStudents();
        List<User> waitlisted = event.getWaitlistedStudents();

        if (confirmed.contains(student)) {
            confirmed.remove(student);
            
            // Auto-promote the first waitlisted candidate (First-In, First-Out queue management)
            if (!waitlisted.isEmpty()) {
                User promotedStudent = waitlisted.remove(0);
                confirmed.add(promotedStudent);
            }
            eventRepository.save(event);
        } else if (waitlisted.contains(student)) {
            waitlisted.remove(student);
            eventRepository.save(event);
        }
    }

    private String calculateHmacSha256(String data, String secret) {
        try {
            byte[] hashSecret = secret.getBytes(StandardCharsets.UTF_8);
            SecretKeySpec secretKey = new SecretKeySpec(hashSecret, "HmacSHA256");
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(secretKey);
            byte[] hmacBytes = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hmacBytes);
        } catch (Exception e) {
            throw new RuntimeException("Failed to compute cryptographic signature token.", e);
        }
    }
}