package com.internverse.service;

import com.internverse.dto.SubmissionRequest;
import com.internverse.dto.SubmissionResponse;
import com.internverse.exception.ApiException;
import com.internverse.model.Role;
import com.internverse.model.Submission;
import com.internverse.model.SubmissionStatus;
import com.internverse.model.Task;
import com.internverse.model.User;
import com.internverse.repository.SubmissionRepository;
import com.internverse.repository.TaskRepository;
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
public class SubmissionService {

    private final SubmissionRepository submissionRepository;
    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final SecurityUtil securityUtil;
    private final EmailNotificationService emailNotificationService;

    @Transactional(readOnly = true)
    public List<SubmissionResponse> listForCurrentUser() {
        User u = securityUtil.requireCurrentUser();
        if (u.getRole() == Role.ADMIN || u.getRole() == Role.MENTOR) {
            return submissionRepository.findAll().stream()
                    .map(SubmissionResponse::fromEntity).collect(Collectors.toList());
        }
        return submissionRepository.findByUser(u).stream()
                .map(SubmissionResponse::fromEntity).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public SubmissionResponse get(Long id) {
        Submission s = submissionRepository.findById(id)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Submission not found"));
        User current = securityUtil.requireCurrentUser();
        if (current.getRole() == Role.INTERN && !s.getUser().getId().equals(current.getId())) {
            throw new ApiException(HttpStatus.FORBIDDEN, "Access denied");
        }
        return SubmissionResponse.fromEntity(s);
    }

    /**
     * Admin assigns a task to an intern (creates submission row).
     */
    @Transactional
    public SubmissionResponse assign(SubmissionRequest req) {
        User admin = securityUtil.requireCurrentUser();
        if (admin.getRole() != Role.ADMIN) {
            throw new ApiException(HttpStatus.FORBIDDEN, "Only admins can assign tasks");
        }
        if (req.getInternId() == null) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "internId is required for assignment");
        }
        User intern = userRepository.findById(req.getInternId())
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Intern not found"));
        if (intern.getRole() != Role.INTERN) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Target user must be an intern");
        }
        Task task = taskRepository.findById(req.getTaskId())
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Task not found"));
        submissionRepository.findByUserAndTask(intern, task).ifPresent(s -> {
            throw new ApiException(HttpStatus.CONFLICT, "Task already assigned to this intern");
        });
        Submission sub = Submission.builder()
                .user(intern)
                .task(task)
                .submissionLink(null)
                .status(SubmissionStatus.ASSIGNED)
                .build();
        sub = submissionRepository.save(sub);
        emailNotificationService.sendTaskAssigned(intern.getEmail(), intern.getName(), task.getTitle());
        return SubmissionResponse.fromEntity(sub);
    }

    /**
     * Intern submits or updates work link.
     */
    @Transactional
    public SubmissionResponse submitWork(Long submissionId, String submissionLink) {
        User current = securityUtil.requireCurrentUser();
        if (current.getRole() != Role.INTERN) {
            throw new ApiException(HttpStatus.FORBIDDEN, "Only interns submit work");
        }
        Submission s = submissionRepository.findById(submissionId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Submission not found"));
        if (!s.getUser().getId().equals(current.getId())) {
            throw new ApiException(HttpStatus.FORBIDDEN, "Not your submission");
        }
        if (submissionLink == null || submissionLink.isBlank()) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "submissionLink required");
        }
        s.setSubmissionLink(submissionLink);
        s.setStatus(SubmissionStatus.SUBMITTED);
        final Submission saved = submissionRepository.save(s);
        final String taskTitle = saved.getTask().getTitle();
        userRepository.findByRole(Role.ADMIN).forEach(a ->
                emailNotificationService.sendTaskCompleted(
                        a.getEmail(), a.getName(), taskTitle, current.getName()));
        return SubmissionResponse.fromEntity(saved);
    }
}
