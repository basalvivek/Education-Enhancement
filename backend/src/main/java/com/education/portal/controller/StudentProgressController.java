package com.education.portal.controller;

import com.education.portal.dto.StudentProgressResponse;
import com.education.portal.dto.TopicProgressResponse;
import com.education.portal.service.StudentProgressService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/student")
@PreAuthorize("hasRole('STUDENT')")
@RequiredArgsConstructor
public class StudentProgressController {

    private final StudentProgressService studentProgressService;

    @GetMapping("/progress")
    public ResponseEntity<StudentProgressResponse> getMyProgress() {
        return ResponseEntity.ok(studentProgressService.getMyProgress());
    }

    @GetMapping("/progress/topic/{topicId}")
    public ResponseEntity<TopicProgressResponse> getProgressByTopic(@PathVariable Long topicId) {
        return ResponseEntity.ok(studentProgressService.getProgressByTopic(topicId));
    }
}
