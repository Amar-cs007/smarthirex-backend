package com.smarthirex.controller;

import com.smarthirex.entity.CodingAnswer;
import com.smarthirex.entity.Question;
import com.smarthirex.entity.Result;
import com.smarthirex.entity.User;
import com.smarthirex.entity.Job;
import com.smarthirex.service.AssessmentService;
import com.smarthirex.service.NotificationService;
import com.smarthirex.service.EmailService;
import com.smarthirex.service.UserService;
import com.smarthirex.service.JobService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/assessment")
@CrossOrigin(origins = {
    "http://localhost:3000",
    "http://localhost:3001",
    "http://localhost:3002"
})
public class AssessmentController {

    @Autowired
    private AssessmentService service;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private EmailService emailService;

    @Autowired
    private UserService userService;

    @Autowired
    private JobService jobService;

    // Add MCQ or Coding Question
    @PostMapping("/question")
    public Question addQuestion(
            @RequestBody Question question) {
        return service.saveQuestion(question);
    }

    // Get all questions for a job
    @GetMapping("/questions/{jobId}")
    public List<Question> getQuestions(
            @PathVariable Long jobId) {
        return service.getQuestionsByJob(jobId);
    }

    // Get only MCQ questions
    @GetMapping("/questions/{jobId}/mcq")
    public List<Question> getMCQ(
            @PathVariable Long jobId) {
        return service.getMCQByJob(jobId);
    }

    // Get only Coding questions
    @GetMapping("/questions/{jobId}/coding")
    public List<Question> getCoding(
            @PathVariable Long jobId) {
        return service.getCodingByJob(jobId);
    }

    // Delete a question
    @DeleteMapping("/question/{id}")
    public String deleteQuestion(
            @PathVariable Long id) {
        service.deleteQuestion(id);
        return "Question deleted!";
    }

    // Submit MCQ answers
    @PostMapping("/submit/{userId}/{jobId}")
    public Result submit(
            @PathVariable Long userId,
            @PathVariable Long jobId,
            @RequestBody Map<Long, String> answers) {

        Result result = service.submitAssessment(
            userId, jobId, answers);

        // Send notification
        notificationService.send(userId,
            "MCQ Assessment submitted! Score: "
            + result.getScore() + "/"
            + result.getTotalQuestions());

        // Send email notification
        try {
            User user = userService.getUserById(userId);
            Job job = jobService.getJobById(jobId);
            if (user != null && job != null
                    && user.getEmail() != null) {
                emailService.sendAssessmentResultEmail(
                    user.getEmail(),
                    user.getName(),
                    job.getTitle(),
                    result.getScore(),
                    result.getTotalQuestions()
                );
            }
        } catch (Exception e) {
            System.out.println(
                "Assessment email failed: "
                + e.getMessage());
        }

        return result;
    }

    // Submit coding answer
    @PostMapping("/submit/coding")
    public CodingAnswer submitCoding(
            @RequestBody CodingAnswer answer) {
        return service.submitCodingAnswer(answer);
    }

    // Get coding answers by user and job
    @GetMapping("/coding/answers/{userId}/{jobId}")
    public List<CodingAnswer> getCodingAnswers(
            @PathVariable Long userId,
            @PathVariable Long jobId) {
        return service.getCodingAnswersByUserAndJob(
            userId, jobId);
    }

    // Get all coding answers for a job (Admin)
    @GetMapping("/coding/answers/job/{jobId}")
    public List<CodingAnswer> getCodingAnswersByJob(
            @PathVariable Long jobId) {
        return service.getCodingAnswersByJob(jobId);
    }

    // Get results by user
    @GetMapping("/results/user/{userId}")
    public List<Result> getUserResults(
            @PathVariable Long userId) {
        return service.getResultsByUser(userId);
    }

    // Get results by job (Admin)
    @GetMapping("/results/job/{jobId}")
    public List<Result> getJobResults(
            @PathVariable Long jobId) {
        return service.getResultsByJob(jobId);
    }

    // Get all results (Admin)
    @GetMapping("/results")
    public List<Result> getAllResults() {
        return service.getAllResults();
    }
}