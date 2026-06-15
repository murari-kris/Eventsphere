package com.college.events.controller;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PageController {

    private ResponseEntity<Resource> serveStaticHtml(String fileName) {
        Resource resource = new ClassPathResource("static/" + fileName);
        if (!resource.exists()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok()
                .contentType(MediaType.TEXT_HTML)
                .body(resource);
    }

    @GetMapping("/login")
    public ResponseEntity<Resource> loginPage() {
        return serveStaticHtml("Login.html");
    }

    @GetMapping("/register")
    public ResponseEntity<Resource> registerPage() {
        return serveStaticHtml("Register.html");
    }

    @GetMapping("/events")
    public ResponseEntity<Resource> eventsPage() {
        return serveStaticHtml("events.html");
    }

    @GetMapping("/dashboard")
    public ResponseEntity<Resource> dashboardPage() {
        return serveStaticHtml("dashboard.html");
    }
}