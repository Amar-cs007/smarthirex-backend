package com.smarthirex.controller;

import com.smarthirex.entity.Application;
import com.smarthirex.entity.User;
import com.smarthirex.entity.Job;
import com.smarthirex.service.ApplicationService;
import com.smarthirex.service.NotificationService;
import com.smarthirex.service.EmailService;
import com.smarthirex.service.UserService;
import com.smarthirex.service.JobService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/applications")
@CrossOrigin(origins = {
    "http://localhost:3000",
    "http://localhost:3001",
    "http://localhost:3002"
})
public class ApplicationController {

    @Autowired
    private ApplicationService service;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private EmailService emailService;

    @Autowired
    private UserService userService;

    @Autowired
    private JobService jobService;

    // Candidate applies for a job
    @PostMapping("/{userId}/{jobId}")
    public ResponseEntity<?> apply(
            @PathVariable Long userId,
            @PathVariable Long jobId) {
        try {
            Application app = service.apply(userId, jobId);

            // Send notification
            notificationService.send(userId,
                "✅ You have successfully applied for Job #"
                + jobId);

            // Send email confirmation
            try {
                User user = userService.getUserById(userId);
                Job job = jobService.getJobById(jobId);
                if (user != null && job != null
                        && user.getEmail() != null) {
                    emailService.sendEmail(
                        user.getEmail(),
                        "Application Submitted — SmartHireX",
                        "<div style='font-family:Arial;"
                        + "padding:20px;'>"
                        + "<h2 style='color:#4f46e5;'>"
                        + "SmartHireX</h2>"
                        + "<h3>Dear " + user.getName()
                        + ",</h3>"
                        + "<p>Your application for <b>"
                        + job.getTitle()
                        + "</b> has been submitted "
                        + "successfully!</p>"
                        + "<p>📍 Location: "
                        + job.getLocation() + "</p>"
                        + "<p>You can track your "
                        + "application status in the "
                        + "My Applications section.</p>"
                        + "<br><p>Best Regards,<br>"
                        + "<b>SmartHireX Team</b></p>"
                        + "</div>"
                    );
                }
            } catch (Exception e) {
                System.out.println(
                    "Application email failed: "
                    + e.getMessage());
            }

            return ResponseEntity.ok(app);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                .body(e.getMessage());
        }
    }

    // Get applications by candidate
    @GetMapping("/user/{userId}")
    public List<Application> getByUser(
            @PathVariable Long userId) {
        return service.getByUser(userId);
    }

    // Get applications by job
    @GetMapping("/job/{jobId}")
    public List<Application> getByJob(
            @PathVariable Long jobId) {
        return service.getByJob(jobId);
    }

    // Get all applications (Admin)
    @GetMapping
    public List<Application> getAll() {
        return service.getAll();
    }

    // Admin updates application status
    @PutMapping("/{id}/status")
    public Application updateStatus(
            @PathVariable Long id,
            @RequestParam String status) {

        Application app = service.updateStatus(id, status);

        if (app != null) {
            try {
                User user = userService.getUserById(
                    app.getUserId());
                Job job = jobService.getJobById(
                    app.getJobId());

                if (user != null && job != null) {

                    // Send notification to candidate
                    if (status.equals("SHORTLISTED")) {

                        notificationService.send(
                            user.getId(),
                            "🎉 Congratulations! You are "
                            + "shortlisted for HR Round "
                            + "for " + job.getTitle() + "!"
                        );

                        // Send HR selection email
                        if (user.getEmail() != null) {
                            emailService.sendSelectionEmail(
                                user.getEmail(),
                                user.getName(),
                                job.getTitle()
                            );
                        }

                    } else if (status.equals("REJECTED")) {

                        notificationService.send(
                            user.getId(),
                            "Your application for "
                            + job.getTitle()
                            + " was not selected. "
                            + "Keep trying! 💪"
                        );

                        // Send rejection email
                        if (user.getEmail() != null) {
                            emailService.sendRejectionEmail(
                                user.getEmail(),
                                user.getName(),
                                job.getTitle()
                            );
                        }
                    }
                }
            } catch (Exception e) {
                System.out.println(
                    "Status email failed: "
                    + e.getMessage());
            }
        }

        return app;
    }
}