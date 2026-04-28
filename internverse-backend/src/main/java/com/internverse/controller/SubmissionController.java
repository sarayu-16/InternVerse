package com.internverse.controller;

import com.internverse.dto.SubmissionRequest;
import com.internverse.dto.SubmissionResponse;
import com.internverse.service.SubmissionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/submissions")
@RequiredArgsConstructor
public class SubmissionController {

    private final SubmissionService submissionService;

    @GetMapping
    public ResponseEntity<List<SubmissionResponse>> list() {
        return ResponseEntity.ok(submissionService.listForCurrentUser());
    }

    @GetMapping("/{id}")
    public ResponseEntity<SubmissionResponse> get(@PathVariable Long id) {
        return ResponseEntity.ok(submissionService.get(id));
    }

    @PostMapping("/assign")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SubmissionResponse> assign(@Valid @RequestBody SubmissionRequest request) {
        return ResponseEntity.ok(submissionService.assign(request));
    }

    @PatchMapping("/{id}/submit")
    @PreAuthorize("hasRole('INTERN')")
    public ResponseEntity<SubmissionResponse> submit(
            @PathVariable Long id,
            @RequestBody Map<String, String> body) {
        String link = body.get("submissionLink");
        return ResponseEntity.ok(submissionService.submitWork(id, link));
    }
}
