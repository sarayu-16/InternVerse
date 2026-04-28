package com.internverse.controller;

import com.internverse.dto.EvaluationRequest;
import com.internverse.dto.EvaluationResponse;
import com.internverse.service.EvaluationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/evaluations")
@RequiredArgsConstructor
public class EvaluationController {

    private final EvaluationService evaluationService;

    @GetMapping
    public ResponseEntity<List<EvaluationResponse>> list() {
        return ResponseEntity.ok(evaluationService.listAll());
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','MENTOR')")
    public ResponseEntity<EvaluationResponse> create(@Valid @RequestBody EvaluationRequest request) {
        return ResponseEntity.ok(evaluationService.create(request));
    }
}
