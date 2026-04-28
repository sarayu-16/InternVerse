package com.internverse.dto;

import com.internverse.model.Task;
import com.internverse.model.TaskStatus;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TaskResponse {
    private Long id;
    private String title;
    private String description;
    private String category;
    private String deadline;
    private Long postedById;
    private String postedByName;
    private TaskStatus status;

    public static TaskResponse fromEntity(Task t) {
        return TaskResponse.builder()
                .id(t.getId())
                .title(t.getTitle())
                .description(t.getDescription())
                .category(t.getCategory())
                .deadline(t.getDeadline() != null ? t.getDeadline().toString() : null)
                .postedById(t.getPostedBy() != null ? t.getPostedBy().getId() : null)
                .postedByName(t.getPostedBy() != null ? t.getPostedBy().getName() : null)
                .status(t.getStatus())
                .build();
    }
}
