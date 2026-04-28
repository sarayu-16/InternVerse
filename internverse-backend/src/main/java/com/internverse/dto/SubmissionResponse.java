package com.internverse.dto;

import com.internverse.model.Submission;
import com.internverse.model.SubmissionStatus;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SubmissionResponse {
    private Long id;
    private Long userId;
    private String userName;
    private Long taskId;
    private String taskTitle;
    private String submissionLink;
    private SubmissionStatus status;

    public static SubmissionResponse fromEntity(Submission s) {
        return SubmissionResponse.builder()
                .id(s.getId())
                .userId(s.getUser().getId())
                .userName(s.getUser().getName())
                .taskId(s.getTask().getId())
                .taskTitle(s.getTask().getTitle())
                .submissionLink(s.getSubmissionLink())
                .status(s.getStatus())
                .build();
    }
}
