package com.smarthirex.service;

import com.smarthirex.entity.Job;
import com.smarthirex.repository.JobRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class JobService {

    @Autowired
    private JobRepository repo;

    public List<Job> getAllJobs() {
        return repo.findAll();
    }

    public List<Job> getOpenJobs() {
        return repo.findByStatus("OPEN");
    }

    public Job getJobById(Long id) {
        return repo.findById(id).orElse(null);
    }

    public Job saveJob(Job job) {
        if (job.getStatus() == null) job.setStatus("OPEN");
        return repo.save(job);
    }

    public Job updateJob(Long id, Job updatedJob) {
        Job job = repo.findById(id).orElse(null);
        if (job != null) {
            job.setTitle(updatedJob.getTitle());
            job.setDescription(updatedJob.getDescription());
            job.setLocation(updatedJob.getLocation());
            job.setStatus(updatedJob.getStatus());
            return repo.save(job);
        }
        return null;
    }

    public void deleteJob(Long id) {
        repo.deleteById(id);
    }
}
