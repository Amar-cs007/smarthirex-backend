package com.smarthirex.service;

import com.smarthirex.entity.TestCase;
import com.smarthirex.entity.TestCaseResult;
import com.smarthirex.repository.TestCaseRepository;
import com.smarthirex.repository.TestCaseResultRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.*;

@Service
public class TestCaseService {

    @Autowired
    private TestCaseRepository testCaseRepo;

    @Autowired
    private TestCaseResultRepository resultRepo;

    @Autowired
    private CodeExecutionService codeExecutionService;

    public TestCase addTestCase(TestCase testCase) {
        return testCaseRepo.save(testCase);
    }

    public List<TestCase> getTestCases(Long questionId) {
        return testCaseRepo.findByQuestionId(questionId);
    }

    public List<TestCase> getVisibleTestCases(Long questionId) {
        return testCaseRepo.findByQuestionIdAndIsHidden(
            questionId, false);
    }

    public void deleteTestCase(Long id) {
        testCaseRepo.deleteById(id);
    }

    public Map<String, Object> runAllTestCases(
            Long userId, Long questionId,
            String code, String language) {

        List<TestCase> testCases =
            testCaseRepo.findByQuestionId(questionId);

        if (testCases.isEmpty()) {
            Map<String, Object> result = new HashMap<>();
            result.put("message",
                "No test cases available");
            result.put("passed", 0);
            result.put("total", 0);
            return result;
        }

        int passed = 0;
        int total = testCases.size();
        List<Map<String, Object>> results = new ArrayList<>();

        for (TestCase tc : testCases) {
            Map<String, Object> tcResult =
                codeExecutionService.runTestCase(
                    code, language,
                    tc.getInput(), tc.getExpectedOutput());

            boolean isPassed = (boolean) tcResult
                .getOrDefault("passed", false);
            if (isPassed) passed++;

            // Save result
            TestCaseResult tcr = new TestCaseResult();
            tcr.setUserId(userId);
            tcr.setQuestionId(questionId);
            tcr.setTestCaseId(tc.getId());
            tcr.setSubmittedCode(code);
            tcr.setLanguage(language);
            tcr.setActualOutput(tcResult
                .getOrDefault("output", "").toString());
            tcr.setPassed(isPassed);
            tcr.setStatus(tcResult
                .getOrDefault("status", "").toString());
            resultRepo.save(tcr);

            Map<String, Object> display = new HashMap<>();
            display.put("testCaseId", tc.getId());
            display.put("input",
                tc.isHidden() ? "Hidden" : tc.getInput());
            display.put("expectedOutput",
                tc.isHidden() ? "Hidden"
                : tc.getExpectedOutput());
            display.put("actualOutput",
                tc.isHidden() && !isPassed
                ? "Wrong Answer" : tcResult
                .getOrDefault("output", "").toString());
            display.put("passed", isPassed);
            display.put("status", tcResult
                .getOrDefault("status", ""));
            display.put("hidden", tc.isHidden());
            results.add(display);
        }

        Map<String, Object> summary = new HashMap<>();
        summary.put("passed", passed);
        summary.put("total", total);
        summary.put("score",
            Math.round((passed * 100.0) / total));
        summary.put("results", results);
        return summary;
    }
}
