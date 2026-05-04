package com.education.portal.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class HealthController {

    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> health() {
        return ResponseEntity.ok(Map.of(
            "status", "UP",
            "service", "Education Enhancement Portal",
            "timestamp", LocalDateTime.now().toString(),
            "endpoints", Map.of(
                "login",    "POST /api/auth/login",
                "register", "POST /api/auth/register",
                "admin",    "GET  /api/admin/category  (requires ADMIN token)",
                "exam",     "POST /api/exam/start      (requires STUDENT token)",
                "progress", "GET  /api/student/progress (requires STUDENT token)"
            )
        ));
    }
}
