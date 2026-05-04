package com.education.portal.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.util.List;

@Data
@AllArgsConstructor
public class StudentProgressResponse {
    private String studentName;
    private String studentEmail;
    private int totalExams;
    private double overallAverage;
    private List<TopicProgressResponse> topics;
    private List<String> weakTopics;
    private List<String> recommendations;
}
