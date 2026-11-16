package com.smarthirex.service;

import com.smarthirex.entity.CodingAnswer;
import com.smarthirex.entity.Question;
import com.smarthirex.entity.Result;
import com.smarthirex.repository.CodingAnswerRepository;
import com.smarthirex.repository.QuestionRepository;
import com.smarthirex.repository.ResultRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Map;

@Service
public class AssessmentService {

    @Autowired
    private QuestionRepository questionRepo;

    @Autowired
    private ResultRepository resultRepo;

    @Autowired
    private CodingAnswerRepository codingAnswerRepo;

    public Question saveQuestion(Question q) {
        return questionRepo.save(q);
    }

    public List<Question> getQuestionsByJob(Long jobId) {
        return questionRepo.findByJobId(jobId);
    }

    public List<Question> getMCQByJob(Long jobId) {
        return questionRepo.findByJobIdAndQuestionType(
            jobId, "MCQ");
    }

    public List<Question> getCodingByJob(Long jobId) {
        return questionRepo.findByJobIdAndQuestionType(
            jobId, "CODING");
    }

    public void deleteQuestion(Long id) {
        questionRepo.deleteById(id);
    }

    public Result submitAssessment(Long userId, Long jobId,
            Map<Long, String> answers) {
        List<Question> questions =
            questionRepo.findByJobIdAndQuestionType(jobId, "MCQ");
        int score = 0;
        for (Question q : questions) {
            String submitted = answers.get(q.getId());
            if (submitted != null &&
                submitted.equals(q.getCorrectAnswer())) {
                score++;
            }
        }
        Result result = new Result();
        result.setUserId(userId);
        result.setJobId(jobId);
        result.setScore(score);
        result.setTotalQuestions(questions.size());
        return resultRepo.save(result);
    }

    public CodingAnswer submitCodingAnswer(
            CodingAnswer answer) {
        return codingAnswerRepo.save(answer);
    }

    public List<CodingAnswer> getCodingAnswersByUserAndJob(
            Long userId, Long jobId) {
        return codingAnswerRepo.findByUserIdAndJobId(
            userId, jobId);
    }

    public List<CodingAnswer> getCodingAnswersByJob(
            Long jobId) {
        return codingAnswerRepo.findByJobId(jobId);
    }

    public List<Result> getResultsByUser(Long userId) {
        return resultRepo.findByUserId(userId);
    }

    public List<Result> getResultsByJob(Long jobId) {
        return resultRepo.findByJobId(jobId);
    }

    public List<Result> getAllResults() {
        return resultRepo.findAll();
    }
}