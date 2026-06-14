package com.college.events.dto;

public class RegisterRequest {
    private String name;
    private String email;
    private String password;
    private String phone;
    private String branch;
    private String year;
    private String skills; // Takes the comma-separated string from frontend (e.g., "FRONTEND,BACKEND")

    // Getters and Setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getBranch() { return branch; }
    public void setBranch(String branch) { this.branch = branch; }

    public String getYear() { return year; }
    public void setYear(String year) { this.year = year; }

    public String getSkills() { return skills; }
    public void setSkills(String skills) { this.skills = skills; }
}