package com.college.events.service;

import com.college.events.dto.LoginRequest;
import com.college.events.dto.RegisterRequest;
import com.college.events.entity.User; // Ensure this matches your actual User entity location
import com.college.events.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class AuthService {

    private final UserRepository userRepository;

    public AuthService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Complete Registration Logic
     */
    public Map<String, Object> register(RegisterRequest request) {
        // 1. Data Validation Checks
        if (request.getName() == null || request.getName().isBlank() ||
            request.getEmail() == null || request.getEmail().isBlank() ||
            request.getPassword() == null || request.getPassword().isBlank()) {
            throw new IllegalArgumentException("Name, email and password are required");
        }

        if (request.getPassword().length() < 6) {
            throw new IllegalArgumentException("Password must be at least 6 characters");
        }

        // 2. Prevent duplicate entries
        String normalizedEmail = request.getEmail().toLowerCase().trim();
        if (userRepository.existsByEmail(normalizedEmail)) {
            throw new IllegalArgumentException("An account with this email already exists");
        }

        // 3. Create and Save User Entity
        User user = new User();
        user.setName(request.getName().trim());
        user.setEmail(normalizedEmail);
        user.setPassword(request.getPassword()); // Wrap with password encoder if active
        user.setPhone(request.getPhone() != null ? request.getPhone().trim() : null);
        user.setBranch(request.getBranch());
        user.setYear(request.getYear());
        user.setSkills(request.getSkills());
        user.setRole("STUDENT"); // Default role assignments 

        User savedUser = userRepository.save(user);

        // 4. Generate Response Map for frontend LocalStorage
        String mockToken = "jwt-session-token-for-" + savedUser.getId();

        Map<String, Object> response = new HashMap<>();
        response.put("accessToken", mockToken);
        response.put("id", savedUser.getId());
        response.put("name", savedUser.getName());
        response.put("email", savedUser.getEmail());
        response.put("phone", savedUser.getPhone());
        response.put("branch", savedUser.getBranch());
        response.put("year", savedUser.getYear());
        response.put("skills", savedUser.getSkills());
        response.put("role", savedUser.getRole().toLowerCase());

        return response;
    }

    /**
     * Complete Login Logic
     */
    public Map<String, Object> login(LoginRequest request) {
        // 1. Basic input validation
        if (request.getEmail() == null || request.getEmail().isBlank() ||
            request.getPassword() == null || request.getPassword().isBlank()) {
            throw new IllegalArgumentException("Please fill all fields");
        }

        // 2. Fetch user profile from database
        String normalizedEmail = request.getEmail().toLowerCase().trim();
        Optional<User> userOpt = userRepository.findByEmail(normalizedEmail);

        if (userOpt.isEmpty()) {
            throw new IllegalArgumentException("Invalid credentials");
        }

        User user = userOpt.get();

        // 3. Verify password match
        if (!request.getPassword().equals(user.getPassword())) {
            throw new IllegalArgumentException("Invalid credentials");
        }

        // 4. Build matching session map response
        String mockToken = "jwt-session-token-for-" + user.getId();

        Map<String, Object> response = new HashMap<>();
        response.put("accessToken", mockToken);
        response.put("id", user.getId());
        response.put("name", user.getName());
        response.put("email", user.getEmail());
        response.put("phone", user.getPhone());
        response.put("branch", user.getBranch());
        response.put("year", user.getYear());
        response.put("skills", user.getSkills());
        response.put("role", user.getRole().toLowerCase());

        return response;
    }
}