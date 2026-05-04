package com.education.portal.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.util.List;

@Data
@AllArgsConstructor
public class ExamSubmitResponse {
    private Long examId;
    private int score;
    private int total;
    private double percentage;
    private String grade;
    private List<ExamAnswerResult> answers;
}
