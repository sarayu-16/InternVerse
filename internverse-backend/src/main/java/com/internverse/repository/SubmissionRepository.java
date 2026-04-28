package com.internverse.repository;

import com.internverse.model.Submission;
import com.internverse.model.SubmissionStatus;
import com.internverse.model.Task;
import com.internverse.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SubmissionRepository extends JpaRepository<Submission, Long> {

    List<Submission> findByUser(User user);

    List<Submission> findByTask(Task task);

    Optional<Submission> findByUserAndTask(User user, Task task);

    List<Submission> findByStatus(SubmissionStatus status);
}
