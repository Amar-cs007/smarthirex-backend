package com.smarthirex.service;

import com.smarthirex.entity.Application;
import com.smarthirex.repository.ApplicationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ApplicationService {

    @Autowired
    private ApplicationRepository repo;

    public Application apply(Long userId, Long jobId) {
        if (repo.existsByUserIdAndJobId(userId, jobId)) {
            throw new RuntimeException("Already applied for this job!");
        }
        Application app = new Application();
        app.setUserId(userId);
        app.setJobId(jobId);
        return repo.save(app);
    }

    public List<Application> getByUser(Long userId) {
        return repo.findByUserId(userId);
    }

    public List<Application> getByJob(Long jobId) {
        return repo.findByJobId(jobId);
    }

    public List<Application> getAll() {
        return repo.findAll();
    }

    public Application updateStatus(Long id, String status) {
        Application app = repo.findById(id).orElse(null);
        if (app != null) {
            app.setStatus(status);
            return repo.save(app);
        }
        return null;
    }
}
