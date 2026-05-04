package com.education.portal.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class QuestionRequest {

    @NotBlank
    private String questionText;

    // "MCQ" or "DESCRIPTIVE"
    @NotBlank
    private String questionType;

    // MCQ fields (required when questionType = MCQ)
    private String optionA;
    private String optionB;
    private String optionC;
    private String optionD;

    @Pattern(regexp = "[ABCD]", message = "correctAnswer must be A, B, C, or D")
    private String correctAnswer;

    // Descriptive field (required when questionType = DESCRIPTIVE)
    private String descriptiveAnswer;

    private String explanation;

    @NotNull
    private Long topicId;
}
