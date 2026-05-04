package com.education.portal.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ExamAnswerResult {
    private Long questionId;
    private String questionText;
    private String studentAnswer;
    private String correctAnswer;
    private boolean correct;
    private String explanation;
}
