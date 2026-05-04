package com.education.portal.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.util.List;

@Data
public class ExamSubmitRequest {

    @NotNull
    private Long examId;

    @NotEmpty
    private List<ExamAnswerSubmit> answers;
}
