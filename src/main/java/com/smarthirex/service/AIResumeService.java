package com.smarthirex.service;

import com.smarthirex.entity.Job;
import com.smarthirex.entity.ResumeScreening;
import com.smarthirex.entity.User;
import com.smarthirex.repository.JobRepository;
import com.smarthirex.repository.ResumeScreeningRepository;
import com.smarthirex.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.*;

@Service
public class AIResumeService {

    @Autowired
    private ResumeScreeningRepository screeningRepo;

    @Autowired
    private JobRepository jobRepo;

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private EmailService emailService;

    @Autowired
    private NotificationService notificationService;

    // AI Resume Screening using keyword matching
    // (In production use OpenAI API)
    public ResumeScreening screenResume(
            Long userId, Long jobId, String resumeText) {

        Job job = jobRepo.findById(jobId).orElse(null);
        User user = userRepo.findById(userId).orElse(null);

        if (job == null || user == null) return null;

        // Calculate AI score based on keywords
        int score = calculateResumeScore(
            resumeText, job.getTitle(), job.getDescription());

        String feedback = generateFeedback(score, resumeText);
        String status = score >= 60 ? "SELECTED" : "REJECTED";

        // Check if already screened
        Optional<ResumeScreening> existing =
            screeningRepo.findByUserIdAndJobId(userId, jobId);

        ResumeScreening screening = existing.orElse(
            new ResumeScreening());
        screening.setUserId(userId);
        screening.setJobId(jobId);
        screening.setResumeText(resumeText);
        screening.setAiScore(score);
        screening.setAiFeedback(feedback);
        screening.setStatus(status);

        ResumeScreening saved = screeningRepo.save(screening);

        // Send notifications
        if (status.equals("SELECTED")) {
            notificationService.send(userId,
                "✅ Your resume passed AI screening for "
                + job.getTitle()
                + "! Proceed to take the assessment test.");
            if (user.getEmail() != null) {
                emailService.sendEmail(
                    user.getEmail(),
                    "Resume Screening Passed — SmartHireX",
                    "<div style='font-family:Arial;padding:20px;'>"
                    + "<h2 style='color:#4f46e5;'>SmartHireX</h2>"
                    + "<h3>Dear " + user.getName() + ",</h3>"
                    + "<p>Your resume has been screened by our AI "
                    + "system for <b>" + job.getTitle() + "</b>.</p>"
                    + "<div style='background:#dcfce7;"
                    + "padding:15px;border-radius:8px;'>"
                    + "<h3 style='color:#166534;margin:0;'>"
                    + "✅ SELECTED — AI Score: " + score + "%</h3>"
                    + "</div>"
                    + "<p>" + feedback + "</p>"
                    + "<p>Please proceed to take the "
                    + "assessment test.</p>"
                    + "<br><p>Best Regards,<br>"
                    + "<b>SmartHireX Team</b></p></div>"
                );
            }
        } else {
            notificationService.send(userId,
                "❌ Your resume did not meet the requirements "
                + "for " + job.getTitle()
                + ". AI Score: " + score + "%");
            if (user.getEmail() != null) {
                emailService.sendEmail(
                    user.getEmail(),
                    "Resume Screening Update — SmartHireX",
                    "<div style='font-family:Arial;padding:20px;'>"
                    + "<h2 style='color:#4f46e5;'>SmartHireX</h2>"
                    + "<h3>Dear " + user.getName() + ",</h3>"
                    + "<p>Your resume has been screened by our AI "
                    + "system for <b>" + job.getTitle() + "</b>.</p>"
                    + "<div style='background:#fee2e2;"
                    + "padding:15px;border-radius:8px;'>"
                    + "<h3 style='color:#991b1b;margin:0;'>"
                    + "❌ NOT SELECTED — AI Score: " + score
                    + "%</h3></div>"
                    + "<p>" + feedback + "</p>"
                    + "<br><p>Best Regards,<br>"
                    + "<b>SmartHireX Team</b></p></div>"
                );
            }
        }
        return saved;
    }

    private int calculateResumeScore(
            String resumeText, String jobTitle,
            String jobDescription) {
        if (resumeText == null || resumeText.isEmpty())
            return 0;

        String resume = resumeText.toLowerCase();
        String title = jobTitle.toLowerCase();
        String desc = jobDescription != null
            ? jobDescription.toLowerCase() : "";

        int score = 0;

        // Technical keywords based on job
        List<String> techKeywords = getTechKeywords(title);
        List<String> softKeywords = Arrays.asList(
            "experience", "project", "developed",
            "implemented", "designed", "led", "managed",
            "team", "collaborated", "achieved"
        );
        List<String> educationKeywords = Arrays.asList(
            "b.tech", "b.e", "bachelor", "master", "mca",
            "bca", "computer science", "information technology",
            "engineering", "degree"
        );

        // Check tech keywords (50 points)
        int techFound = 0;
        for (String kw : techKeywords) {
            if (resume.contains(kw)) techFound++;
        }
        score += Math.min(50,
            (techFound * 50) / Math.max(techKeywords.size(), 1));

        // Check soft keywords (25 points)
        int softFound = 0;
        for (String kw : softKeywords) {
            if (resume.contains(kw)) softFound++;
        }
        score += Math.min(25,
            (softFound * 25) / softKeywords.size());

        // Check education (25 points)
        int eduFound = 0;
        for (String kw : educationKeywords) {
            if (resume.contains(kw)) eduFound++;
        }
        score += Math.min(25,
            eduFound > 0 ? 25 : 0);

        return Math.min(100, score);
    }

    private List<String> getTechKeywords(String jobTitle) {
        if (jobTitle.contains("java") ||
            jobTitle.contains("backend") ||
            jobTitle.contains("software")) {
            return Arrays.asList(
                "java", "spring", "springboot", "spring boot",
                "hibernate", "jpa", "rest", "api", "mysql",
                "sql", "maven", "git", "microservices"
            );
        } else if (jobTitle.contains("frontend") ||
                   jobTitle.contains("react") ||
                   jobTitle.contains("ui")) {
            return Arrays.asList(
                "react", "javascript", "html", "css",
                "typescript", "redux", "node", "npm",
                "responsive", "bootstrap", "git"
            );
        } else if (jobTitle.contains("fullstack") ||
                   jobTitle.contains("full stack")) {
            return Arrays.asList(
                "java", "react", "spring", "javascript",
                "mysql", "html", "css", "rest", "api",
                "git", "node"
            );
        } else if (jobTitle.contains("data") ||
                   jobTitle.contains("analyst")) {
            return Arrays.asList(
                "python", "sql", "excel", "tableau",
                "power bi", "machine learning", "pandas",
                "numpy", "statistics", "visualization"
            );
        } else if (jobTitle.contains("devops")) {
            return Arrays.asList(
                "docker", "kubernetes", "jenkins", "aws",
                "linux", "ci/cd", "git", "ansible",
                "terraform", "monitoring"
            );
        } else {
            return Arrays.asList(
                "java", "python", "sql", "git",
                "programming", "software", "development",
                "agile", "testing"
            );
        }
    }

    private String generateFeedback(
            int score, String resumeText) {
        if (score >= 80) {
            return "Excellent resume! Strong technical skills "
                + "and relevant experience detected. "
                + "You are a great fit for this role.";
        } else if (score >= 60) {
            return "Good resume with relevant skills. "
                + "You meet the basic requirements "
                + "for this position.";
        } else if (score >= 40) {
            return "Your resume shows some relevant skills "
                + "but lacks key technical experience "
                + "required for this role.";
        } else {
            return "Your resume does not match the "
                + "requirements for this position. "
                + "Please improve your technical skills "
                + "and update your resume.";
        }
    }

    public List<ResumeScreening> getScreeningsByJob(
            Long jobId) {
        return screeningRepo.findByJobId(jobId);
    }

    public Optional<ResumeScreening> getScreeningByUserAndJob(
            Long userId, Long jobId) {
        return screeningRepo.findByUserIdAndJobId(userId, jobId);
    }

    public List<ResumeScreening> getScreeningsByUser(
            Long userId) {
        return screeningRepo.findByUserId(userId);
    }
}