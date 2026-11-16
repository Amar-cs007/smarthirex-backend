package com.smarthirex.controller;

import com.smarthirex.entity.TestCase;
import com.smarthirex.service.TestCaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/testcases")
@CrossOrigin(origins = {
    "http://localhost:3000",
    "http://localhost:3001",
    "http://localhost:3002"
})
public class TestCaseController {

    @Autowired
    private TestCaseService service;

    @PostMapping
    public TestCase addTestCase(
            @RequestBody TestCase testCase) {
        return service.addTestCase(testCase);
    }

    @GetMapping("/{questionId}")
    public List<TestCase> getTestCases(
            @PathVariable Long questionId) {
        return service.getVisibleTestCases(questionId);
    }

    @GetMapping("/all/{questionId}")
    public List<TestCase> getAllTestCases(
            @PathVariable Long questionId) {
        return service.getTestCases(questionId);
    }

    @DeleteMapping("/{id}")
    public String deleteTestCase(@PathVariable Long id) {
        service.deleteTestCase(id);
        return "Test case deleted!";
    }

    @PostMapping("/run/{userId}/{questionId}")
    public Map<String, Object> runTestCases(
            @PathVariable Long userId,
            @PathVariable Long questionId,
            @RequestBody Map<String, String> body) {
        return service.runAllTestCases(
            userId, questionId,
            body.get("code"), body.get("language"));
    }
}
