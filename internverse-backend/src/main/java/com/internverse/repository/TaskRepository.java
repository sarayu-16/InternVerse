package com.internverse.repository;

import com.internverse.model.Task;
import com.internverse.model.TaskStatus;
import com.internverse.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {

    List<Task> findByPostedBy(User postedBy);

    List<Task> findByStatus(TaskStatus status);
}
