package com.college.events.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    private String phone;
    private String branch;
    private String year;
    
    @Column(columnDefinition = "TEXT")
    private String skills;

    @Column(nullable = false)
    private String role = "STUDENT";

    // Standard Explicit Getters
    public Long getId() { return this.id; }
    public String getName() { return this.name; }
    public String getEmail() { return this.email; }
    public String getPassword() { return this.password; }
    public String getPhone() { return this.phone; }
    public String getBranch() { return this.branch; }
    public String getYear() { return this.year; }
    public String getSkills() { return this.skills; }
    public String getRole() { return this.role; }

    // Standard Explicit Setters
    public void setId(Long id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setEmail(String email) { this.email = email; }
    public void setPassword(String password) { this.password = password; }
    public void setPhone(String phone) { this.phone = phone; }
    public void setBranch(String branch) { this.branch = branch; }
    public void setYear(String year) { this.year = year; }
    public void setSkills(String skills) { this.skills = skills; }
    public void setRole(String role) { this.role = role; }
}