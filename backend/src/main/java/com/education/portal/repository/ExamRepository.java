package com.education.portal.repository;

import com.education.portal.model.Exam;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ExamRepository extends JpaRepository<Exam, Long> {

    List<Exam> findByStudentIdOrderByStartedAtDesc(Long studentId);

    Optional<Exam> findByIdAndStudentId(Long id, Long studentId);

    @Query("""
        SELECT e FROM Exam e
        WHERE e.student.id = :studentId
          AND e.content.topic.id = :topicId
          AND e.submittedAt IS NOT NULL
        ORDER BY e.submittedAt DESC
    """)
    List<Exam> findCompletedByStudentAndTopic(@Param("studentId") Long studentId,
                                               @Param("topicId") Long topicId);

    @Query("""
        SELECT e FROM Exam e
        WHERE e.student.id = :studentId
          AND e.submittedAt IS NOT NULL
        ORDER BY e.submittedAt DESC
    """)
    List<Exam> findAllCompletedByStudent(@Param("studentId") Long studentId);
}
