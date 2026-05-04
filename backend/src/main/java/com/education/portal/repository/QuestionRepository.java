package com.education.portal.repository;

import com.education.portal.model.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

public interface QuestionRepository extends JpaRepository<Question, Long> {
    List<Question> findByTopicId(Long topicId);
    long countByTopicId(Long topicId);

    @Query(value = "SELECT * FROM questions WHERE topic_id = :topicId ORDER BY RANDOM() LIMIT :limit", nativeQuery = true)
    List<Question> findRandomByTopicId(Long topicId, int limit);
}
