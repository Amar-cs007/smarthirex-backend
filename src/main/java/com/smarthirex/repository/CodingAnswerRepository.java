package com.smarthirex.repository;

import com.smarthirex.entity.CodingAnswer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface CodingAnswerRepository
        extends JpaRepository<CodingAnswer, Long> {
    List<CodingAnswer> findByUserIdAndJobId(
        Long userId, Long jobId);
    List<CodingAnswer> findByJobId(Long jobId);
}
