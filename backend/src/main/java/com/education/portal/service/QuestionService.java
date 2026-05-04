package com.education.portal.service;

import com.education.portal.dto.QuestionRequest;
import com.education.portal.dto.QuestionResponse;
import com.education.portal.model.Question;
import com.education.portal.model.Topic;
import com.education.portal.repository.QuestionRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class QuestionService {

    private final QuestionRepository questionRepository;
    private final TopicService topicService;

    public List<QuestionResponse> getByTopicId(Long topicId) {
        return questionRepository.findByTopicId(topicId).stream()
                .map(QuestionResponse::from)
                .toList();
    }

    public QuestionResponse getById(Long id) {
        return QuestionResponse.from(findOrThrow(id));
    }

    public QuestionResponse create(QuestionRequest request) {
        validate(request);
        Topic topic = topicService.findOrThrow(request.getTopicId());

        boolean descriptive = "DESCRIPTIVE".equalsIgnoreCase(request.getQuestionType());

        Question question = Question.builder()
                .questionType(descriptive ? "DESCRIPTIVE" : "MCQ")
                .questionText(request.getQuestionText())
                .optionA(descriptive ? null : request.getOptionA())
                .optionB(descriptive ? null : request.getOptionB())
                .optionC(descriptive ? null : request.getOptionC())
                .optionD(descriptive ? null : request.getOptionD())
                .correctAnswer(descriptive ? null : request.getCorrectAnswer())
                .descriptiveAnswer(descriptive ? request.getDescriptiveAnswer() : null)
                .explanation(request.getExplanation())
                .topic(topic)
                .build();
        return QuestionResponse.from(questionRepository.save(question));
    }

    public QuestionResponse update(Long id, QuestionRequest request) {
        validate(request);
        Question question = findOrThrow(id);
        boolean descriptive = "DESCRIPTIVE".equalsIgnoreCase(request.getQuestionType());

        question.setQuestionType(descriptive ? "DESCRIPTIVE" : "MCQ");
        question.setQuestionText(request.getQuestionText());
        question.setOptionA(descriptive ? null : request.getOptionA());
        question.setOptionB(descriptive ? null : request.getOptionB());
        question.setOptionC(descriptive ? null : request.getOptionC());
        question.setOptionD(descriptive ? null : request.getOptionD());
        question.setCorrectAnswer(descriptive ? null : request.getCorrectAnswer());
        question.setDescriptiveAnswer(descriptive ? request.getDescriptiveAnswer() : null);
        question.setExplanation(request.getExplanation());
        return QuestionResponse.from(questionRepository.save(question));
    }

    public void delete(Long id) {
        findOrThrow(id);
        questionRepository.deleteById(id);
    }

    public Question findOrThrow(Long id) {
        return questionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Question not found: " + id));
    }

    public List<Question> findRandomByTopic(Long topicId, int count) {
        return questionRepository.findRandomByTopicId(topicId, count);
    }

    private void validate(QuestionRequest r) {
        if ("DESCRIPTIVE".equalsIgnoreCase(r.getQuestionType())) {
            if (r.getDescriptiveAnswer() == null || r.getDescriptiveAnswer().isBlank())
                throw new IllegalArgumentException("Descriptive answer is required for DESCRIPTIVE questions.");
        } else {
            if (r.getOptionA() == null || r.getOptionA().isBlank())
                throw new IllegalArgumentException("Option A is required for MCQ questions.");
            if (r.getOptionB() == null || r.getOptionB().isBlank())
                throw new IllegalArgumentException("Option B is required for MCQ questions.");
            if (r.getCorrectAnswer() == null || r.getCorrectAnswer().isBlank())
                throw new IllegalArgumentException("Correct answer is required for MCQ questions.");
        }
    }
}
