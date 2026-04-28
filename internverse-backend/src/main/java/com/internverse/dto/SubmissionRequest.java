package com.internverse.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class SubmissionRequest {

    @NotNull
    private Long taskId;

    /** Intern user id — required when admin assigns */
    private Long internId;

    private String submissionLink;
}
