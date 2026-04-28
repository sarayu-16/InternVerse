package com.internverse.dto;

import com.internverse.model.Evaluation;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class EvaluationResponse {
    private Long id;
    private Long internId;
    private String internName;
    private Long evaluatorId;
    private String evaluatorName;
    private Long submissionId;
    private BigDecimal score;
    private String feedback;
    private String createdAt;

    public static EvaluationResponse fromEntity(Evaluation e) {
        return EvaluationResponse.builder()
                .id(e.getId())
                .internId(e.getUser().getId())
                .internName(e.getUser().getName())
                .evaluatorId(e.getEvaluator() != null ? e.getEvaluator().getId() : null)
                .evaluatorName(e.getEvaluator() != null ? e.getEvaluator().getName() : null)
                .submissionId(e.getSubmission() != null ? e.getSubmission().getId() : null)
                .score(e.getScore())
                .feedback(e.getFeedback())
                .createdAt(e.getCreatedAt() != null ? e.getCreatedAt().toString() : null)
                .build();
    }
}
