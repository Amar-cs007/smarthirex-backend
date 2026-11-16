package com.smarthirex.controller;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.*;

@RestController
@RequestMapping("/api/resume")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:3001"})
public class ResumeController {

    private final String UPLOAD_DIR = "resumes/";

    @PostMapping("/upload/{userId}")
    public ResponseEntity<String> upload(
                        @PathVariable Long userId,
                        @RequestParam("file") MultipartFile file)
                        throws IOException {
        Files.createDirectories(Paths.get(UPLOAD_DIR));
        String filename = "user_" + userId + "_"
                        + file.getOriginalFilename();
        Path path = Paths.get(UPLOAD_DIR + filename);
        Files.write(path, file.getBytes());
        return ResponseEntity.ok("Resume uploaded: " + filename);
    }

    @GetMapping("/download/{filename}")
    public ResponseEntity<Resource> download(
                        @PathVariable String filename)
                        throws MalformedURLException {
        Path path = Paths.get(UPLOAD_DIR + filename);
        Resource resource = new UrlResource(path.toUri());
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + filename + "\"")
                .body(resource);
    }
}