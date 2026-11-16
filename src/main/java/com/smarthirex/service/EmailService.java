package com.smarthirex.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import jakarta.mail.internet.MimeMessage;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    public void sendEmail(String to, String subject,
            String body) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper =
                new MimeMessageHelper(message, true);
            helper.setFrom(fromEmail);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(body, true);
            mailSender.send(message);
            System.out.println("Email sent to: " + to);
        } catch (Exception e) {
            System.out.println("Email failed: " + e.getMessage());
        }
    }

    public void sendSelectionEmail(String to,
            String name, String jobTitle) {
        String subject = "🎉 Congratulations! Selected for HR Round — SmartHireX";
        String body = "<div style='font-family:Arial;padding:20px;'>"
            + "<h2 style='color:#4f46e5;'>SmartHireX</h2>"
            + "<h3>Dear " + name + ",</h3>"
            + "<p>Congratulations! 🎉</p>"
            + "<p>You have been <b style='color:green;'>SELECTED</b> "
            + "for the <b>" + jobTitle + "</b> position.</p>"
            + "<p>You have successfully cleared:</p>"
            + "<ul>"
            + "<li>✅ Round 1: AI Resume Screening</li>"
            + "<li>✅ Round 2: MCQ + Coding Assessment</li>"
            + "</ul>"
            + "<p>Our HR team will contact you shortly "
            + "for the HR Round interview.</p>"
            + "<br><p>Best Regards,<br>"
            + "<b>SmartHireX Hiring Team</b></p>"
            + "</div>";
        sendEmail(to, subject, body);
    }

    public void sendRejectionEmail(String to,
            String name, String jobTitle) {
        String subject = "SmartHireX — Application Update";
        String body = "<div style='font-family:Arial;padding:20px;'>"
            + "<h2 style='color:#4f46e5;'>SmartHireX</h2>"
            + "<h3>Dear " + name + ",</h3>"
            + "<p>Thank you for applying for "
            + "<b>" + jobTitle + "</b>.</p>"
            + "<p>After careful review, we regret to inform "
            + "you that you have <b style='color:red;'>"
            + "not been selected</b> at this time.</p>"
            + "<p>We encourage you to keep improving "
            + "your skills and apply again in the future.</p>"
            + "<br><p>Best Regards,<br>"
            + "<b>SmartHireX Hiring Team</b></p>"
            + "</div>";
        sendEmail(to, subject, body);
    }

    public void sendRegistrationEmail(String to,
            String name) {
        String subject = "Welcome to SmartHireX! 🎯";
        String body = "<div style='font-family:Arial;padding:20px;'>"
            + "<h2 style='color:#4f46e5;'>SmartHireX</h2>"
            + "<h3>Welcome, " + name + "! 👋</h3>"
            + "<p>Your account has been created successfully.</p>"
            + "<p>You can now:</p>"
            + "<ul>"
            + "<li>Browse available job openings</li>"
            + "<li>Upload your resume for AI screening</li>"
            + "<li>Take online assessments</li>"
            + "<li>Track your application status</li>"
            + "</ul>"
            + "<p>Login at: <a href='http://localhost:3000'>"
            + "SmartHireX Portal</a></p>"
            + "<br><p>Best Regards,<br>"
            + "<b>SmartHireX Team</b></p>"
            + "</div>";
        sendEmail(to, subject, body);
    }

    public void sendAssessmentResultEmail(String to,
            String name, String jobTitle,
            int score, int total) {
        int percentage = (score * 100) / total;
        String status = percentage >= 60
            ? "PASSED ✅" : "FAILED ❌";
        String subject = "SmartHireX — Assessment Result";
        String body = "<div style='font-family:Arial;padding:20px;'>"
            + "<h2 style='color:#4f46e5;'>SmartHireX</h2>"
            + "<h3>Dear " + name + ",</h3>"
            + "<p>Your assessment result for "
            + "<b>" + jobTitle + "</b>:</p>"
            + "<div style='background:#f3f4f6;"
            + "padding:20px;border-radius:8px;'>"
            + "<h2 style='color:#4f46e5;margin:0;'>"
            + percentage + "%</h2>"
            + "<p>Score: " + score + "/" + total + "</p>"
            + "<p>Status: <b>" + status + "</b></p>"
            + "</div>"
            + "<br><p>Best Regards,<br>"
            + "<b>SmartHireX Team</b></p>"
            + "</div>";
        sendEmail(to, subject, body);
    }
}