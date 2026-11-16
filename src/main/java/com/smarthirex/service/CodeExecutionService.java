package com.smarthirex.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.*;

@Service
public class CodeExecutionService {

    @Value("${judge0.api.key}")
    private String apiKey;

    @Value("${judge0.api.url}")
    private String apiUrl;

    private final RestTemplate restTemplate =
        new RestTemplate();

    // Language IDs for Judge0
    private static final Map<String, Integer> LANGUAGE_MAP =
        new HashMap<>();
    static {
        LANGUAGE_MAP.put("Java", 62);
        LANGUAGE_MAP.put("Python", 71);
        LANGUAGE_MAP.put("C++", 54);
        LANGUAGE_MAP.put("JavaScript", 63);
        LANGUAGE_MAP.put("C", 50);
    }

    public Map<String, Object> executeCode(
            String code, String language, String input) {
        try {
            int languageId = LANGUAGE_MAP.getOrDefault(
                language, 62);

            // Create submission
            Map<String, Object> submission = new HashMap<>();
            submission.put("source_code",
                java.util.Base64.getEncoder()
                .encodeToString(code.getBytes()));
            submission.put("language_id", languageId);
            if (input != null && !input.isEmpty()) {
                submission.put("stdin",
                    java.util.Base64.getEncoder()
                    .encodeToString(input.getBytes()));
            }
            submission.put("base64_encoded", true);
            submission.put("wait", true);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("X-RapidAPI-Key", apiKey);
            headers.set("X-RapidAPI-Host",
                "judge0-ce.p.rapidapi.com");

            HttpEntity<Map<String, Object>> request =
                new HttpEntity<>(submission, headers);

            ResponseEntity<Map> response = restTemplate.exchange(
                apiUrl + "/submissions?base64_encoded=true&wait=true",
                HttpMethod.POST,
                request,
                Map.class
            );

            Map<String, Object> result = new HashMap<>();
            Map body = response.getBody();

            if (body != null) {
                String stdout = body.get("stdout") != null
                    ? new String(java.util.Base64.getDecoder()
                        .decode(body.get("stdout").toString()))
                    : "";
                String stderr = body.get("stderr") != null
                    ? new String(java.util.Base64.getDecoder()
                        .decode(body.get("stderr").toString()))
                    : "";
                String compileOutput =
                    body.get("compile_output") != null
                    ? new String(java.util.Base64.getDecoder()
                        .decode(body.get("compile_output")
                        .toString()))
                    : "";

                result.put("output", stdout.trim());
                result.put("error",
                    !stderr.isEmpty() ? stderr :
                    !compileOutput.isEmpty() ?
                    compileOutput : "");
                result.put("status",
                    body.get("status") != null
                    ? ((Map)body.get("status")).get("description")
                    : "Unknown");
                result.put("time",
                    body.get("time") != null
                    ? body.get("time") : "0");
            }
            return result;

        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("output", "");
            error.put("error", "Execution failed: "
                + e.getMessage());
            error.put("status", "Error");
            return error;
        }
    }

    public Map<String, Object> runTestCase(
            String code, String language,
            String input, String expectedOutput) {
        Map<String, Object> result =
            executeCode(code, language, input);
        String actualOutput = result.get("output")
            .toString().trim();
        boolean passed = actualOutput.equals(
            expectedOutput.trim());
        result.put("passed", passed);
        result.put("expectedOutput", expectedOutput);
        result.put("actualOutput", actualOutput);
        return result;
    }
}
