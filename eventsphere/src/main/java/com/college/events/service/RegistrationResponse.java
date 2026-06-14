package com.college.events.service;

public class RegistrationResponse {
    private String status;
    private String message;
    private String ticketToken;

    public RegistrationResponse(String status, String message, String ticketToken) {
        this.status = status;
        this.message = message;
        this.ticketToken = ticketToken;
    }

    // Getters and Setters
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public String getTicketToken() { return ticketToken; }
    public void setTicketToken(String ticketToken) { this.ticketToken = ticketToken; }
}