package com.smarthirex.repository;

import com.smarthirex.entity.ResumeScreening;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface ResumeScreeningRepository
        extends JpaRepository<ResumeScreening, Long> {
    List<ResumeScreening> findByJobId(Long jobId);
    Optional<ResumeScreening> findByUserIdAndJobId(
        Long userId, Long jobId);
    List<ResumeScreening> findByUserId(Long userId);
}
