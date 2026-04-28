package com.internverse.repository;

import com.internverse.model.Certificate;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CertificateRepository extends JpaRepository<Certificate, Long> {

    Optional<Certificate> findByVerificationCode(String verificationCode);

    java.util.List<Certificate> findByStudent_Id(Long studentId);

    Optional<Certificate> findBySubmission_Id(Long submissionId);
}
