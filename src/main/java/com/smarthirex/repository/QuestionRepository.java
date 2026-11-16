package com.smarthirex.repository;

import com.smarthirex.entity.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface QuestionRepository
        extends JpaRepository<Question, Long> {

    List<Question> findByJobId(Long jobId);

    List<Question> findByJobIdAndQuestionType(
        Long jobId, String questionType);
}
