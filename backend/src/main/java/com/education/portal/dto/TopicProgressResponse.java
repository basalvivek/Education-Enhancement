package com.education.portal.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.util.List;

@Data
@AllArgsConstructor
public class TopicProgressResponse {
    private Long topicId;
    private String topicName;
    private String className;
    private String categoryName;
    private int totalAttempts;
    private double averageScore;
    private double averagePercentage;
    private boolean weak;
    private List<ExamHistoryEntry> history;

    @Data
    @AllArgsConstructor
    public static class ExamHistoryEntry {
        private Long examId;
        private String contentTitle;
        private int score;
        private int total;
        private double percentage;
        private String submittedAt;
    }
}
