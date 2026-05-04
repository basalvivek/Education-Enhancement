package com.education.portal.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class ExamAnswerSubmit {

    @NotNull
    private Long questionId;

    @NotNull
    @Pattern(regexp = "[ABCD]", message = "answer must be A, B, C, or D")
    private String answer;
}
