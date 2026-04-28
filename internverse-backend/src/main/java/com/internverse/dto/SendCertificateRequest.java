package com.internverse.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SendCertificateRequest {
    @NotNull(message = "Certificate ID is required")
    private Long certificateId;
    
    private String customMessage;
}
