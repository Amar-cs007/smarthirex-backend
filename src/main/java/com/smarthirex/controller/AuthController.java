package com.smarthirex.controller;

import com.smarthirex.config.JwtUtil;
import com.smarthirex.dto.AuthRequest;
import com.smarthirex.dto.AuthResponse;
import com.smarthirex.entity.User;
import com.smarthirex.repository.UserRepository;
import com.smarthirex.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = {
    "http://localhost:3000",
    "http://localhost:3001",
    "http://localhost:3002"
})
public class AuthController {

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private EmailService emailService;

    @PostMapping("/register")
    public ResponseEntity<?> register(
            @RequestBody AuthRequest request) {
        if (userRepo.existsByEmail(request.getEmail())) {
            return ResponseEntity.badRequest()
                .body("Email already registered!");
        }
        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(
            request.getPassword()));
        user.setRole(request.getRole() != null
            ? request.getRole() : "CANDIDATE");
        userRepo.save(user);

        // Send welcome email
        try {
            emailService.sendRegistrationEmail(
                request.getEmail(), request.getName());
        } catch (Exception e) {
            System.out.println("Email failed: "
                + e.getMessage());
        }

        return ResponseEntity.ok(
            "Registered successfully!");
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(
            @RequestBody AuthRequest request) {
        User user = userRepo.findByEmail(
            request.getEmail());
        if (user == null) {
            return ResponseEntity.badRequest()
                .body("User not found!");
        }
        if (!passwordEncoder.matches(
                request.getPassword(),
                user.getPassword())) {
            return ResponseEntity.badRequest()
                .body("Invalid password!");
        }
        String token = jwtUtil.generateToken(
            user.getEmail(), user.getRole());
        return ResponseEntity.ok(
            new AuthResponse(
                token,
                user.getRole(),
                user.getName(),
                user.getId()
            ));
    }
}