package com.education.portal.controller;

import com.education.portal.dto.ContentRequest;
import com.education.portal.dto.ContentResponse;
import com.education.portal.service.ContentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/content")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class AdminContentController {

    private final ContentService contentService;

    @GetMapping
    public ResponseEntity<List<ContentResponse>> getByTopicId(@RequestParam Long topicId) {
        return ResponseEntity.ok(contentService.getByTopicId(topicId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ContentResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(contentService.getById(id));
    }

    @PostMapping
    public ResponseEntity<ContentResponse> create(@Valid @RequestBody ContentRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(contentService.create(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ContentResponse> update(@PathVariable Long id,
                                                   @Valid @RequestBody ContentRequest request) {
        return ResponseEntity.ok(contentService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        contentService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
