package com.internverse.service;

import com.internverse.dto.TaskRequest;
import com.internverse.dto.TaskResponse;
import com.internverse.exception.ApiException;
import com.internverse.model.Role;
import com.internverse.model.Task;
import com.internverse.model.TaskStatus;
import com.internverse.model.User;
import com.internverse.repository.TaskRepository;
import com.internverse.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;
    private final SecurityUtil securityUtil;

    @Transactional(readOnly = true)
    public List<TaskResponse> listAll() {
        return taskRepository.findAll().stream().map(TaskResponse::fromEntity).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public TaskResponse get(Long id) {
        return taskRepository.findById(id).map(TaskResponse::fromEntity)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Task not found"));
    }

    @Transactional
    public TaskResponse create(TaskRequest req) {
        User admin = securityUtil.requireCurrentUser();
        if (admin.getRole() != Role.ADMIN) {
            throw new ApiException(HttpStatus.FORBIDDEN, "Only admins can create tasks");
        }
        Task task = Task.builder()
                .title(req.getTitle())
                .description(req.getDescription())
                .category(req.getCategory())
                .deadline(req.getDeadline())
                .postedBy(admin)
                .status(req.getStatus() != null ? req.getStatus() : TaskStatus.ACTIVE)
                .build();
        return TaskResponse.fromEntity(taskRepository.save(task));
    }

    @Transactional
    public TaskResponse update(Long id, TaskRequest req) {
        User current = securityUtil.requireCurrentUser();
        if (current.getRole() != Role.ADMIN) {
            throw new ApiException(HttpStatus.FORBIDDEN, "Only admins can update tasks");
        }
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Task not found"));
        if (req.getTitle() != null) task.setTitle(req.getTitle());
        if (req.getDescription() != null) task.setDescription(req.getDescription());
        if (req.getCategory() != null) task.setCategory(req.getCategory());
        if (req.getDeadline() != null) task.setDeadline(req.getDeadline());
        if (req.getStatus() != null) task.setStatus(req.getStatus());
        return TaskResponse.fromEntity(taskRepository.save(task));
    }

    @Transactional
    public void delete(Long id) {
        User current = securityUtil.requireCurrentUser();
        if (current.getRole() != Role.ADMIN) {
            throw new ApiException(HttpStatus.FORBIDDEN, "Only admins can delete tasks");
        }
        if (!taskRepository.existsById(id)) {
            throw new ApiException(HttpStatus.NOT_FOUND, "Task not found");
        }
        taskRepository.deleteById(id);
    }
}
