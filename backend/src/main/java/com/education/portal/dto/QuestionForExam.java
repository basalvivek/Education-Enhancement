package com.education.portal.dto;

import com.education.portal.model.Question;
import lombok.Data;

@Data
public class QuestionForExam {
    private Long id;
    private String questionText;
    private String optionA;
    private String optionB;
    private String optionC;
    private String optionD;

    public static QuestionForExam from(Question q) {
        QuestionForExam dto = new QuestionForExam();
        dto.id = q.getId();
        dto.questionText = q.getQuestionText();
        dto.optionA = q.getOptionA();
        dto.optionB = q.getOptionB();
        dto.optionC = q.getOptionC();
        dto.optionD = q.getOptionD();
        return dto;
    }
}
