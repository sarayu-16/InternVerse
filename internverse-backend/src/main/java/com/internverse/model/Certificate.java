package com.internverse.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "certificates", indexes = @Index(columnList = "verification_code", unique = true))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Certificate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private User student;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "submission_id")
    private Submission submission;

    @Column(name = "issue_date", nullable = false)
    private LocalDate issueDate;

    @Column(name = "certificate_link", columnDefinition = "TEXT")
    private String certificateLink;

    @Column(name = "verification_code", nullable = false, unique = true, length = 64)
    private String verificationCode;
}
