package com.smarthirex.controller;

import com.smarthirex.entity.ResumeScreening;
import com.smarthirex.service.AIResumeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/screening")
@CrossOrigin(origins = {
    "http://localhost:3000",
    "http://localhost:3001",
    "http://localhost:3002"
})
public class ResumeScreeningController {

    @Autowired
    private AIResumeService aiResumeService;

    @PostMapping("/screen/{userId}/{jobId}")
    public ResumeScreening screenResume(
            @PathVariable Long userId,
            @PathVariable Long jobId,
            @RequestBody Map<String, String> body) {
        return aiResumeService.screenResume(
            userId, jobId, body.get("resumeText"));
    }

    @GetMapping("/job/{jobId}")
    public List<ResumeScreening> getByJob(
            @PathVariable Long jobId) {
        return aiResumeService.getScreeningsByJob(jobId);
    }

    @GetMapping("/user/{userId}")
    public List<ResumeScreening> getByUser(
            @PathVariable Long userId) {
        return aiResumeService.getScreeningsByUser(userId);
    }

    @GetMapping("/check/{userId}/{jobId}")
    public ResumeScreening checkScreening(
            @PathVariable Long userId,
            @PathVariable Long jobId) {
        return aiResumeService
            .getScreeningByUserAndJob(userId, jobId)
            .orElse(null);
    }
}
