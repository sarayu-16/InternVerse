package com.internverse.service;

import com.internverse.model.Role;
import com.internverse.model.SubmissionStatus;
import com.internverse.model.TaskStatus;
import com.internverse.model.User;
import com.internverse.repository.*;
import com.internverse.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final UserRepository userRepository;
    private final TaskRepository taskRepository;
    private final SubmissionRepository submissionRepository;
    private final EvaluationRepository evaluationRepository;
    private final CertificateRepository certificateRepository;
    private final SecurityUtil securityUtil;

    @Transactional(readOnly = true)
    public Map<String, Object> getDashboard() {
        User u = securityUtil.requireCurrentUser();
        return switch (u.getRole()) {
            case ADMIN -> adminSummary();
            case INTERN -> internSummary(u);
            case MENTOR -> mentorSummary(u);
        };
    }

    private Map<String, Object> adminSummary() {
        Map<String, Object> m = new HashMap<>();
        m.put("role", "ADMIN");
        m.put("totalUsers", userRepository.count());
        m.put("interns", userRepository.findByRole(Role.INTERN).size());
        m.put("mentors", userRepository.findByRole(Role.MENTOR).size());
        m.put("totalTasks", taskRepository.count());
        m.put("activeTasks", taskRepository.findByStatus(TaskStatus.ACTIVE).size());
        m.put("submissionsPendingReview", submissionRepository.findByStatus(SubmissionStatus.SUBMITTED).size());
        m.put("evaluations", evaluationRepository.count());
        m.put("certificatesIssued", certificateRepository.count());
        return m;
    }

    private Map<String, Object> internSummary(User u) {
        Map<String, Object> m = new HashMap<>();
        m.put("role", "INTERN");
        long assigned = submissionRepository.findByUser(u).stream()
                .filter(s -> s.getStatus() == SubmissionStatus.ASSIGNED).count();
        long submitted = submissionRepository.findByUser(u).stream()
                .filter(s -> s.getStatus() == SubmissionStatus.SUBMITTED
                        || s.getStatus() == SubmissionStatus.UNDER_REVIEW).count();
        long approved = submissionRepository.findByUser(u).stream()
                .filter(s -> s.getStatus() == SubmissionStatus.APPROVED).count();
        m.put("tasksTotal", submissionRepository.findByUser(u).size());
        m.put("pendingAction", assigned);
        m.put("awaitingReview", submitted);
        m.put("approved", approved);
        m.put("certificates", certificateRepository.findByStudent_Id(u.getId()).size());
        return m;
    }

    private Map<String, Object> mentorSummary(User u) {
        Map<String, Object> m = new HashMap<>();
        m.put("role", "MENTOR");
        m.put("submissionsToReview", submissionRepository.findByStatus(SubmissionStatus.SUBMITTED).size());
        m.put("myEvaluations", evaluationRepository.findByEvaluator(u).size());
        return m;
    }
}
