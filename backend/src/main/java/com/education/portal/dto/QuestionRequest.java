package com.education.portal.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class QuestionRequest {

    @NotBlank
    private String questionText;

    @NotBlank
    private String optionA;

    @NotBlank
    private String optionB;

    private String optionC;

    private String optionD;

    @NotBlank
    @Pattern(regexp = "[ABCD]", message = "correctAnswer must be A, B, C, or D")
    private String correctAnswer;

    private String explanation;

    @NotNull
    private Long topicId;
}
