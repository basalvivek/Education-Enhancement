package com.education.portal.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "exam_answers")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ExamAnswer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "exam_id", nullable = false)
    private Exam exam;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id", nullable = false)
    private Question question;

    @Column(name = "student_answer", length = 1)
    private String studentAnswer;

    @Builder.Default
    @Column(name = "is_correct", nullable = false)
    private boolean correct = false;
}
