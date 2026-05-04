package com.education.portal.dto;

import com.education.portal.model.Question;
import lombok.Data;

@Data
public class QuestionResponse {
    private Long id;
    private String questionType;
    private String questionText;
    private String optionA;
    private String optionB;
    private String optionC;
    private String optionD;
    private String correctAnswer;
    private String descriptiveAnswer;
    private String explanation;
    private Long topicId;
    private String topicName;

    public static QuestionResponse from(Question q) {
        QuestionResponse r = new QuestionResponse();
        r.id = q.getId();
        r.questionType = q.getQuestionType();
        r.questionText = q.getQuestionText();
        r.optionA = q.getOptionA();
        r.optionB = q.getOptionB();
        r.optionC = q.getOptionC();
        r.optionD = q.getOptionD();
        r.correctAnswer = q.getCorrectAnswer();
        r.descriptiveAnswer = q.getDescriptiveAnswer();
        r.explanation = q.getExplanation();
        r.topicId = q.getTopic().getId();
        r.topicName = q.getTopic().getName();
        return r;
    }
}
