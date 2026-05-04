package com.education.portal.controller;

import com.education.portal.dto.TopicRequest;
import com.education.portal.dto.TopicResponse;
import com.education.portal.service.TopicService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/topic")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class AdminTopicController {

    private final TopicService topicService;

    @GetMapping
    public ResponseEntity<List<TopicResponse>> getByClassId(@RequestParam Long classId) {
        return ResponseEntity.ok(topicService.getByClassId(classId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<TopicResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(topicService.getById(id));
    }

    @PostMapping
    public ResponseEntity<TopicResponse> create(@Valid @RequestBody TopicRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(topicService.create(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TopicResponse> update(@PathVariable Long id,
                                                 @Valid @RequestBody TopicRequest request) {
        return ResponseEntity.ok(topicService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        topicService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
