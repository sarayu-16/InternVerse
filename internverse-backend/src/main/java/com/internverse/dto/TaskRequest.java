package com.internverse.dto;

import com.internverse.model.TaskStatus;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.Instant;

@Data
public class TaskRequest {

    @NotBlank
    private String title;

    private String description;
    private String category;
    private Instant deadline;
    private TaskStatus status;
}
