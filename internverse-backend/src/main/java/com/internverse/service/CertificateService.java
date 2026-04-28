package com.internverse.service;

import com.internverse.dto.CertificateResponse;
import com.internverse.exception.ApiException;
import com.internverse.model.Certificate;
import com.internverse.model.Role;
import com.internverse.model.Submission;
import com.internverse.model.User;
import com.internverse.repository.CertificateRepository;
import com.internverse.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CertificateService {

    private final CertificateRepository certificateRepository;
    private final PdfCertificateService pdfCertificateService;
    private final EmailNotificationService emailNotificationService;
    private final SecurityUtil securityUtil;

    @Value("${internverse.upload-dir:./uploads}")
    private String uploadDir;

    @Value("${internverse.public-base-url:http://localhost:8080}")
    private String publicBaseUrl;

    @Transactional
    public void issueForSubmission(Submission submission, User intern) {
        if (certificateRepository.findBySubmission_Id(submission.getId()).isPresent()) {
            return;
        }
        String verificationCode = UUID.randomUUID().toString().replace("-", "");
        String programTitle = submission.getTask().getTitle();
        byte[] pdf = pdfCertificateService.buildCertificatePdf(
                intern.getName(), programTitle, verificationCode, LocalDate.now());
        try {
            Path dir = Paths.get(uploadDir, "certificates");
            Files.createDirectories(dir);
            String filename = "cert-" + intern.getId() + "-" + submission.getId() + ".pdf";
            Path file = dir.resolve(filename);
            Files.write(file, pdf);
            String link = publicBaseUrl.replaceAll("/$", "") + "/api/files/certificates/" + filename;
            Certificate cert = Certificate.builder()
                    .student(intern)
                    .submission(submission)
                    .issueDate(LocalDate.now())
                    .certificateLink(link)
                    .verificationCode(verificationCode)
                    .build();
            certificateRepository.save(cert);
            emailNotificationService.sendCertificateIssued(
                    intern.getEmail(), intern.getName(), verificationCode, link);
        } catch (Exception e) {
            throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, "Could not store certificate: " + e.getMessage());
        }
    }

    @Transactional(readOnly = true)
    public CertificateResponse verify(String code) {
        Certificate c = certificateRepository.findByVerificationCode(code)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Invalid verification code"));
        return CertificateResponse.fromEntity(c);
    }

    @Transactional(readOnly = true)
    public List<CertificateResponse> listMine() {
        User u = securityUtil.requireCurrentUser();
        if (u.getRole() == Role.ADMIN || u.getRole() == Role.MENTOR) {
            return certificateRepository.findAll().stream()
                    .map(CertificateResponse::fromEntity).collect(Collectors.toList());
        }
        return certificateRepository.findByStudent_Id(u.getId()).stream()
                .map(CertificateResponse::fromEntity).collect(Collectors.toList());
    }

    @Transactional
    public void sendCertificateEmailToIntern(Long certificateId) {
        sendCertificateEmailToIntern(certificateId, null);
    }

    @Transactional
    public void sendCertificateEmailToIntern(Long certificateId, String customMessage) {
        Certificate certificate = certificateRepository.findById(certificateId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Certificate not found"));
        
        User student = certificate.getStudent();
        if (student.getEmail() == null || student.getEmail().isBlank()) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Student email not found");
        }

        String subject = "InternVerse: Your Certificate is Ready";
        StringBuilder bodyBuilder = new StringBuilder();
        bodyBuilder.append("Hello ").append(student.getName()).append(",\n\n");
        bodyBuilder.append("Congratulations! Your certificate has been issued for successfully completing your internship program.\n\n");
        
        if (certificate.getSubmission() != null && certificate.getSubmission().getTask() != null) {
            bodyBuilder.append("Program: ").append(certificate.getSubmission().getTask().getTitle()).append("\n");
        }
        
        bodyBuilder.append("Issue Date: ").append(certificate.getIssueDate()).append("\n");
        bodyBuilder.append("Verification Code: ").append(certificate.getVerificationCode()).append("\n\n");
        
        if (certificate.getCertificateLink() != null) {
            bodyBuilder.append("You can download your certificate here:\n");
            bodyBuilder.append(certificate.getCertificateLink()).append("\n\n");
        }
        
        if (customMessage != null && !customMessage.isBlank()) {
            bodyBuilder.append("Additional Message:\n");
            bodyBuilder.append(customMessage).append("\n\n");
        }
        
        bodyBuilder.append("Thank you for your dedication and hard work!\n");
        bodyBuilder.append("Best regards,\nThe InternVerse Team");

        emailNotificationService.sendEmail(student.getEmail(), subject, bodyBuilder.toString());
    }
}
