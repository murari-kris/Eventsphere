package com.college.events.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "events")
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private LocalDateTime date;
    private String location;
    private Integer maxSeats;
    private Boolean isConcluded = false;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "event_confirmed_students",
        joinColumns = @JoinColumn(name = "event_id"),
        inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private List<User> confirmedStudents = new ArrayList<>();

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "event_waitlisted_students",
        joinColumns = @JoinColumn(name = "event_id"),
        inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private List<User> waitlistedStudents = new ArrayList<>();

    // Standard Explicit Getters and Setters
    public Long getId() { return this.id; }
    public void setId(Long id) { this.id = id; }
    public String getTitle() { return this.title; }
    public void setTitle(String title) { this.title = title; }
    public LocalDateTime getDate() { return this.date; }
    public void setDate(LocalDateTime date) { this.date = date; }
    public String getLocation() { return this.location; }
    public void setLocation(String location) { this.location = location; }
    public Integer getMaxSeats() { return this.maxSeats; }
    public void setMaxSeats(Integer maxSeats) { this.maxSeats = maxSeats; }
    public Boolean getIsConcluded() { return this.isConcluded; }
    public void setIsConcluded(Boolean concluded) { this.isConcluded = concluded; }

    public List<User> getConfirmedStudents() {
        if (this.confirmedStudents == null) this.confirmedStudents = new ArrayList<>();
        return this.confirmedStudents;
    }
    public void setConfirmedStudents(List<User> confirmedStudents) { this.confirmedStudents = confirmedStudents; }

    public List<User> getWaitlistedStudents() {
        if (this.waitlistedStudents == null) this.waitlistedStudents = new ArrayList<>();
        return this.waitlistedStudents;
    }
    public void setWaitlistedStudents(List<User> waitlistedStudents) { this.waitlistedStudents = waitlistedStudents; }
}