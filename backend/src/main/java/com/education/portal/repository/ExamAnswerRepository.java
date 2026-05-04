package com.education.portal.repository;

import com.education.portal.model.ExamAnswer;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ExamAnswerRepository extends JpaRepository<ExamAnswer, Long> {
    List<ExamAnswer> findByExamId(Long examId);
}
