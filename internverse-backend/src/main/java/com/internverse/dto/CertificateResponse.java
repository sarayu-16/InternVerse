package com.internverse.dto;

import com.internverse.model.Certificate;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CertificateResponse {
    private Long id;
    private Long studentId;
    private String studentName;
    private Long submissionId;
    private String issueDate;
    private String certificateLink;
    private String verificationCode;

    public static CertificateResponse fromEntity(Certificate c) {
        return CertificateResponse.builder()
                .id(c.getId())
                .studentId(c.getStudent().getId())
                .studentName(c.getStudent().getName())
                .submissionId(c.getSubmission() != null ? c.getSubmission().getId() : null)
                .issueDate(c.getIssueDate() != null ? c.getIssueDate().toString() : null)
                .certificateLink(c.getCertificateLink())
                .verificationCode(c.getVerificationCode())
                .build();
    }
}
