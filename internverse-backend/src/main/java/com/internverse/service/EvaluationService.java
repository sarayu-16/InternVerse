package com.internverse.service;

import com.internverse.dto.EvaluationRequest;
import com.internverse.dto.EvaluationResponse;
import com.internverse.exception.ApiException;
import com.internverse.model.Evaluation;
import com.internverse.model.Role;
import com.internverse.model.Submission;
import com.internverse.model.SubmissionStatus;
import com.internverse.model.User;
import com.internverse.repository.EvaluationRepository;
import com.internverse.repository.SubmissionRepository;
import com.internverse.repository.UserRepository;
import com.internverse.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EvaluationService {

    private final EvaluationRepository evaluationRepository;
    private final UserRepository userRepository;
    private final SubmissionRepository submissionRepository;
    private final SecurityUtil securityUtil;
    private final CertificateService certificateService;

    @Transactional(readOnly = true)
    public List<EvaluationResponse> listAll() {
        User u = securityUtil.requireCurrentUser();
        if (u.getRole() == Role.INTERN) {
            return evaluationRepository.findByUser(u).stream()
                    .map(EvaluationResponse::fromEntity).collect(Collectors.toList());
        }
        return evaluationRepository.findAll().stream()
                .map(EvaluationResponse::fromEntity).collect(Collectors.toList());
    }

    @Transactional
    public EvaluationResponse create(EvaluationRequest req) {
        User evaluator = securityUtil.requireCurrentUser();
        if (evaluator.getRole() != Role.ADMIN && evaluator.getRole() != Role.MENTOR) {
            throw new ApiException(HttpStatus.FORBIDDEN, "Only admin or mentor can evaluate");
        }
        User intern = userRepository.findById(req.getInternId())
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Intern not found"));
        if (intern.getRole() != Role.INTERN) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Target must be an intern");
        }
        Submission submission = null;
        if (req.getSubmissionId() != null) {
            submission = submissionRepository.findById(req.getSubmissionId())
                    .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Submission not found"));
            if (!submission.getUser().getId().equals(intern.getId())) {
                throw new ApiException(HttpStatus.BAD_REQUEST, "Submission does not belong to intern");
            }
        }
        Evaluation ev = Evaluation.builder()
                .user(intern)
                .evaluator(evaluator)
                .submission(submission)
                .score(req.getScore())
                .feedback(req.getFeedback())
                .build();
        ev = evaluationRepository.save(ev);
        if (submission != null) {
            submission.setStatus(SubmissionStatus.APPROVED);
            submissionRepository.save(submission);
            certificateService.issueForSubmission(submission, intern);
        }
        return EvaluationResponse.fromEntity(ev);
    }
}
