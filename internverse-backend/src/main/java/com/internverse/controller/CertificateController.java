package com.internverse.controller;

import com.internverse.dto.CertificateResponse;
import com.internverse.dto.SendCertificateRequest;
import com.internverse.service.CertificateService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/certificates")
@RequiredArgsConstructor
public class CertificateController {

    private final CertificateService certificateService;

    @GetMapping
    public ResponseEntity<List<CertificateResponse>> list() {
        return ResponseEntity.ok(certificateService.listMine());
    }

    @GetMapping("/verify/{code}")
    public ResponseEntity<CertificateResponse> verify(@PathVariable String code) {
        return ResponseEntity.ok(certificateService.verify(code));
    }

    @PostMapping("/{id}/send-email")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MENTOR')")
    public ResponseEntity<Map<String, String>> sendCertificateEmail(@PathVariable Long id) {
        certificateService.sendCertificateEmailToIntern(id);
        return ResponseEntity.ok(Map.of("message", "Certificate email sent successfully to the intern"));
    }

    @PostMapping("/send")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MENTOR')")
    public ResponseEntity<Map<String, String>> sendCertificate(@Valid @RequestBody SendCertificateRequest request) {
        certificateService.sendCertificateEmailToIntern(request.getCertificateId(), request.getCustomMessage());
        return ResponseEntity.ok(Map.of("message", "Certificate email sent successfully"));
    }
}
