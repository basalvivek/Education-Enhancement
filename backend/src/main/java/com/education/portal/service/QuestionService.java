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
        Topic topic = topicService.findOrThrow(request.getTopicId());

        Question question = Question.builder()
                .questionText(request.getQuestionText())
                .optionA(request.getOptionA())
                .optionB(request.getOptionB())
                .optionC(request.getOptionC())
                .optionD(request.getOptionD())
                .correctAnswer(request.getCorrectAnswer())
                .explanation(request.getExplanation())
                .topic(topic)
                .build();
        return QuestionResponse.from(questionRepository.save(question));
    }

    public QuestionResponse update(Long id, QuestionRequest request) {
        Question question = findOrThrow(id);
        question.setQuestionText(request.getQuestionText());
        question.setOptionA(request.getOptionA());
        question.setOptionB(request.getOptionB());
        question.setOptionC(request.getOptionC());
        question.setOptionD(request.getOptionD());
        question.setCorrectAnswer(request.getCorrectAnswer());
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
}
