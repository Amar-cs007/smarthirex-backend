package com.smarthirex.controller;

import com.smarthirex.entity.User;
import com.smarthirex.repository.UserRepository;
import com.smarthirex.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = {
    "http://localhost:3000",
    "http://localhost:3001",
    "http://localhost:3002"
})
public class ForgotPasswordController {

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private EmailService emailService;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(
            @RequestBody Map<String, String> body) {
        String email = body.get("email");
        User user = userRepo.findByEmail(email);

        if (user == null) {
            return ResponseEntity.badRequest()
                .body("No account found with this email!");
        }

        // Generate new temporary password
        String tempPassword = "Smart@"
            + UUID.randomUUID().toString()
            .substring(0, 6).toUpperCase();

        // Update password in database
        user.setPassword(
            passwordEncoder.encode(tempPassword));
        userRepo.save(user);

        // Send email with new password
        try {
            emailService.sendEmail(
                email,
                "Password Reset — SmartHireX",
                "<div style='font-family:Arial;padding:20px;'>"
                + "<h2 style='color:#4f46e5;'>SmartHireX</h2>"
                + "<h3>Dear " + user.getName() + ",</h3>"
                + "<p>Your password has been reset.</p>"
                + "<div style='background:#f3f4f6;"
                + "padding:16px;border-radius:8px;"
                + "margin:16px 0;'>"
                + "<p style='margin:0;font-size:13px;"
                + "color:#888;'>Your new temporary password:</p>"
                + "<h2 style='margin:8px 0;color:#4f46e5;"
                + "letter-spacing:2px;font-family:monospace;'>"
                + tempPassword + "</h2>"
                + "</div>"
                + "<p>Please login with this password "
                + "and change it immediately.</p>"
                + "<p style='color:#dc2626;font-size:13px;'>"
                + "⚠️ Do not share this password with anyone."
                + "</p>"
                + "<br><p>Best Regards,<br>"
                + "<b>SmartHireX Team</b></p>"
                + "</div>"
            );
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body("Email sending failed! "
                    + "Please try again.");
        }

        return ResponseEntity.ok(
            "Password reset email sent! "
            + "Check your inbox.");
    }
}
