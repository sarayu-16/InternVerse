package com.internverse.controller;

import com.internverse.exception.ApiException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
@RequestMapping("/api/files")
public class FileController {

    @Value("${internverse.upload-dir:./uploads}")
    private String uploadDir;

    @GetMapping("/certificates/{filename}")
    public ResponseEntity<byte[]> downloadCertificate(@PathVariable String filename) {
        try {
            // Security: ensure filename doesn't contain path traversal
            if (filename.contains("..") || filename.contains("/") || filename.contains("\\")) {
                throw new ApiException(org.springframework.http.HttpStatus.BAD_REQUEST, "Invalid filename");
            }

            Path filePath = Paths.get(uploadDir, "certificates", filename);

            if (!Files.exists(filePath)) {
                throw new ApiException(org.springframework.http.HttpStatus.NOT_FOUND, "Certificate not found");
            }

            byte[] fileContent = Files.readAllBytes(filePath);

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_PDF_VALUE)
                    .body(fileContent);
        } catch (IOException e) {
            throw new ApiException(org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR,
                    "Could not read file: " + e.getMessage());
        }
    }
}
