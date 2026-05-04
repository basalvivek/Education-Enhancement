package com.education.portal.controller;

import com.education.portal.dto.ClassRequest;
import com.education.portal.dto.ClassResponse;
import com.education.portal.service.ClassService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/class")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class AdminClassController {

    private final ClassService classService;

    @GetMapping
    public ResponseEntity<List<ClassResponse>> getByCategoryId(@RequestParam Long categoryId) {
        return ResponseEntity.ok(classService.getByCategoryId(categoryId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ClassResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(classService.getById(id));
    }

    @PostMapping
    public ResponseEntity<ClassResponse> create(@Valid @RequestBody ClassRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(classService.create(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ClassResponse> update(@PathVariable Long id,
                                                 @Valid @RequestBody ClassRequest request) {
        return ResponseEntity.ok(classService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        classService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
