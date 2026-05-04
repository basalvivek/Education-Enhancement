package com.education.portal.service;

import com.education.portal.dto.*;
import com.education.portal.model.*;
import com.education.portal.model.Content.ContentType;
import com.education.portal.repository.ExamAnswerRepository;
import com.education.portal.repository.ExamRepository;
import com.education.portal.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ExamService {

    private static final int UNIT_TEST_SIZE  = 25;
    private static final int MOCK_EXAM_SIZE  = 100;
    private static final int PRACTICE_SIZE   = 10;
    private static final double WEAK_THRESHOLD = 60.0;

    private final ExamRepository examRepository;
    private final ExamAnswerRepository examAnswerRepository;
    private final ContentService contentService;
    private final QuestionService questionService;
    private final UserRepository userRepository;

    @Transactional
    public ExamStartResponse startExam(ExamStartRequest request) {
        User student = currentStudent();
        Content content = contentService.findOrThrow(request.getContentId());

        if (content.getContentType() == ContentType.THEORY) {
            throw new IllegalArgumentException("Theory content does not have an exam.");
        }

        int questionCount = switch (content.getContentType()) {
            case UNIT_TEST     -> UNIT_TEST_SIZE;
            case MOCK_EXAM     -> MOCK_EXAM_SIZE;
            case PRACTICE_TEST -> PRACTICE_SIZE;
            default            -> PRACTICE_SIZE;
        };

        List<Question> questions = questionService.findRandomByTopic(
                content.getTopic().getId(), questionCount);

        if (questions.isEmpty()) {
            throw new IllegalStateException("No questions available for this topic yet.");
        }

        Exam exam = Exam.builder()
                .student(student)
                .content(content)
                .total(questions.size())
                .build();
        examRepository.save(exam);

        List<QuestionForExam> questionDtos = questions.stream()
                .map(QuestionForExam::from)
                .toList();

        return new ExamStartResponse(
                exam.getId(),
                content.getTitle(),
                content.getContentType().name(),
                questions.size(),
                questionDtos
        );
    }

    @Transactional
    public ExamSubmitResponse submitExam(ExamSubmitRequest request) {
        User student = currentStudent();

        Exam exam = examRepository.findByIdAndStudentId(request.getExamId(), student.getId())
                .orElseThrow(() -> new EntityNotFoundException("Exam not found: " + request.getExamId()));

        if (exam.getSubmittedAt() != null) {
            throw new IllegalStateException("Exam already submitted.");
        }

        Map<Long, String> submittedAnswers = request.getAnswers().stream()
                .collect(Collectors.toMap(ExamAnswerSubmit::getQuestionId, ExamAnswerSubmit::getAnswer));

        List<Long> questionIds = request.getAnswers().stream()
                .map(ExamAnswerSubmit::getQuestionId)
                .toList();

        Map<Long, Question> questionMap = questionIds.stream()
                .map(questionService::findOrThrow)
                .collect(Collectors.toMap(Question::getId, Function.identity()));

        List<ExamAnswer> examAnswers = questionMap.values().stream().map(question -> {
            String studentAnswer = submittedAnswers.getOrDefault(question.getId(), null);
            boolean correct = question.getCorrectAnswer().equals(studentAnswer);
            return ExamAnswer.builder()
                    .exam(exam)
                    .question(question)
                    .studentAnswer(studentAnswer)
                    .correct(correct)
                    .build();
        }).toList();

        examAnswerRepository.saveAll(examAnswers);

        int score = (int) examAnswers.stream().filter(ExamAnswer::isCorrect).count();
        double percentage = exam.getTotal() > 0 ? (score * 100.0 / exam.getTotal()) : 0;

        exam.setScore(score);
        exam.setSubmittedAt(LocalDateTime.now());
        examRepository.save(exam);

        List<ExamAnswerResult> results = examAnswers.stream().map(ea -> new ExamAnswerResult(
                ea.getQuestion().getId(),
                ea.getQuestion().getQuestionText(),
                ea.getStudentAnswer(),
                ea.getQuestion().getCorrectAnswer(),
                ea.isCorrect(),
                ea.getQuestion().getExplanation()
        )).toList();

        return new ExamSubmitResponse(exam.getId(), score, exam.getTotal(), percentage,
                grade(percentage), results);
    }

    private String grade(double percentage) {
        if (percentage >= 90) return "A*";
        if (percentage >= 80) return "A";
        if (percentage >= 70) return "B";
        if (percentage >= 60) return "C";
        if (percentage >= 50) return "D";
        return "F";
    }

    private User currentStudent() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("User not found: " + email));
    }
}
