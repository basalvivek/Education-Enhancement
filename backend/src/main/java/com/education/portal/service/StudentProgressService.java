package com.education.portal.service;

import com.education.portal.dto.StudentProgressResponse;
import com.education.portal.dto.TopicProgressResponse;
import com.education.portal.dto.TopicProgressResponse.ExamHistoryEntry;
import com.education.portal.model.Exam;
import com.education.portal.model.Topic;
import com.education.portal.model.User;
import com.education.portal.repository.ExamRepository;
import com.education.portal.repository.TopicRepository;
import com.education.portal.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StudentProgressService {

    private static final double WEAK_THRESHOLD = 60.0;
    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private final ExamRepository examRepository;
    private final UserRepository userRepository;
    private final TopicRepository topicRepository;

    public StudentProgressResponse getMyProgress() {
        User student = currentStudent();
        List<Exam> completedExams = examRepository.findAllCompletedByStudent(student.getId());

        if (completedExams.isEmpty()) {
            return new StudentProgressResponse(student.getName(), student.getEmail(),
                    0, 0.0, List.of(), List.of(), List.of("Complete your first exam to see progress."));
        }

        Map<Long, List<Exam>> byTopic = completedExams.stream()
                .collect(Collectors.groupingBy(e -> e.getContent().getTopic().getId()));

        List<TopicProgressResponse> topicProgress = byTopic.entrySet().stream()
                .map(entry -> buildTopicProgress(entry.getKey(), entry.getValue()))
                .sorted(Comparator.comparingDouble(TopicProgressResponse::getAveragePercentage))
                .toList();

        List<String> weakTopics = topicProgress.stream()
                .filter(TopicProgressResponse::isWeak)
                .map(TopicProgressResponse::getTopicName)
                .toList();

        double overallAverage = topicProgress.stream()
                .mapToDouble(TopicProgressResponse::getAveragePercentage)
                .average()
                .orElse(0.0);

        List<String> recommendations = buildRecommendations(topicProgress);

        return new StudentProgressResponse(student.getName(), student.getEmail(),
                completedExams.size(), Math.round(overallAverage * 10.0) / 10.0,
                topicProgress, weakTopics, recommendations);
    }

    public TopicProgressResponse getProgressByTopic(Long topicId) {
        User student = currentStudent();
        Topic topic = topicRepository.findById(topicId)
                .orElseThrow(() -> new EntityNotFoundException("Topic not found: " + topicId));
        List<Exam> exams = examRepository.findCompletedByStudentAndTopic(student.getId(), topicId);
        return buildTopicProgress(topic.getId(), exams);
    }

    private TopicProgressResponse buildTopicProgress(Long topicId, List<Exam> exams) {
        Topic topic = topicRepository.findById(topicId).orElseThrow();

        List<ExamHistoryEntry> history = exams.stream().map(e -> {
            double pct = e.getTotal() > 0 ? (e.getScore() * 100.0 / e.getTotal()) : 0;
            return new ExamHistoryEntry(
                    e.getId(),
                    e.getContent().getTitle(),
                    e.getScore(),
                    e.getTotal(),
                    Math.round(pct * 10.0) / 10.0,
                    e.getSubmittedAt().format(FMT)
            );
        }).toList();

        double avgPct = history.stream().mapToDouble(ExamHistoryEntry::getPercentage).average().orElse(0.0);
        double avgScore = history.stream().mapToDouble(ExamHistoryEntry::getScore).average().orElse(0.0);

        return new TopicProgressResponse(
                topic.getId(),
                topic.getName(),
                topic.getAClass().getName(),
                topic.getAClass().getCategory().getName(),
                exams.size(),
                Math.round(avgScore * 10.0) / 10.0,
                Math.round(avgPct * 10.0) / 10.0,
                avgPct < WEAK_THRESHOLD,
                history
        );
    }

    private List<String> buildRecommendations(List<TopicProgressResponse> topics) {
        List<String> recs = new ArrayList<>();
        topics.stream().filter(TopicProgressResponse::isWeak).forEach(t ->
                recs.add("Revise '" + t.getTopicName() + "' — average score " + t.getAveragePercentage() + "% (below 60%)"));
        topics.stream()
                .filter(t -> !t.isWeak() && t.getAveragePercentage() >= 90)
                .forEach(t -> recs.add("Excellent on '" + t.getTopicName() + "' — consider the Mock Exam!"));
        if (recs.isEmpty()) recs.add("Keep practising to identify weak areas.");
        return recs;
    }

    private User currentStudent() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("User not found: " + email));
    }
}
