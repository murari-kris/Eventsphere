package com.college.events.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "ticket_attendance")
public class TicketAttendance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String userId;     

    @Column(nullable = false)
    private String eventId;    

    @Column(nullable = false)
    private String eventTitle; 

    private boolean attended;  

    // Standard No-Args Constructor required by JPA
    public TicketAttendance() {}

    // Explicit Full Constructor for our custom builder function
    public TicketAttendance(Long id, String userId, String eventId, String eventTitle, boolean attended) {
        this.id = id;
        this.userId = userId;
        this.eventId = eventId;
        this.eventTitle = eventTitle;
        this.attended = attended;
    }

    // --- EXPLICIT GETTERS (Bypasses Lombok to fix IDE red lines) ---
    public Long getId() { return id; }
    public String getUserId() { return userId; }
    public String getEventId() { return eventId; }
    public String getEventTitle() { return eventTitle; }
    public boolean isAttended() { return attended; }

    // --- MANUAL STATIC BUILDER PATTERN APPROACH ---
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long id;
        private String userId;
        private String eventId;
        private String eventTitle;
        private boolean attended;

        public Builder id(Long id) { this.id = id; return this; }
        public Builder userId(String userId) { this.userId = userId; return this; }
        public Builder eventId(String eventId) { this.eventId = eventId; return this; }
        public Builder eventTitle(String eventTitle) { this.eventTitle = eventTitle; return this; }
        public Builder attended(boolean attended) { this.attended = attended; return this; }

        public TicketAttendance build() {
            return new TicketAttendance(id, userId, eventId, eventTitle, attended);
        }
    }
}