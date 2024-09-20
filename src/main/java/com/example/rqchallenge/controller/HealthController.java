package com.example.rqchallenge.controller;

import com.example.rqchallenge.model.HealthResponse;

import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;

@RestController
@RequestMapping("/v1")
@Tag(name = "Health", description = "Health Check")
public class HealthController {

    @GetMapping("/health")
    public ResponseEntity<HealthResponse> healthCheck() {
        HealthResponse healthResponse = new HealthResponse();
        return ResponseEntity.ok(healthResponse);
    }
}

