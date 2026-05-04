package com.education.portal.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.util.List;

@Data
@AllArgsConstructor
public class ExamStartResponse {
    private Long examId;
    private String contentTitle;
    private String contentType;
    private int totalQuestions;
    private List<QuestionForExam> questions;
}
