package com.smarthirex.controller;

import com.smarthirex.entity.Job;
import com.smarthirex.service.JobService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/jobs")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:3001"})
public class JobController {

    @Autowired
    private JobService service;

    @GetMapping
    public List<Job> getAll() {
        return service.getAllJobs();
    }

    @GetMapping("/open")
    public List<Job> getOpenJobs() {
        return service.getOpenJobs();
    }

    @GetMapping("/{id}")
    public Job getById(@PathVariable Long id) {
        return service.getJobById(id);
    }

    @PostMapping
    public Job create(@RequestBody Job job) {
        return service.saveJob(job);
    }

    @PutMapping("/{id}")
    public Job update(@PathVariable Long id, @RequestBody Job job) {
        return service.updateJob(id, job);
    }

    @DeleteMapping("/{id}")
    public String delete(@PathVariable Long id) {
        service.deleteJob(id);
        return "Job deleted!";
    }
}