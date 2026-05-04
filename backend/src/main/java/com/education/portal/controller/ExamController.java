package com.education.portal.controller;

import com.education.portal.dto.ExamStartRequest;
import com.education.portal.dto.ExamStartResponse;
import com.education.portal.dto.ExamSubmitRequest;
import com.education.portal.dto.ExamSubmitResponse;
import com.education.portal.service.ExamService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/exam")
@PreAuthorize("hasRole('STUDENT')")
@RequiredArgsConstructor
public class ExamController {

    private final ExamService examService;

    @PostMapping("/start")
    public ResponseEntity<ExamStartResponse> startExam(@Valid @RequestBody ExamStartRequest request) {
        return ResponseEntity.ok(examService.startExam(request));
    }

    @PostMapping("/submit")
    public ResponseEntity<ExamSubmitResponse> submitExam(@Valid @RequestBody ExamSubmitRequest request) {
        return ResponseEntity.ok(examService.submitExam(request));
    }
}
