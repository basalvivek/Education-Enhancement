package com.education.portal.repository;

import com.education.portal.model.Content;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ContentRepository extends JpaRepository<Content, Long> {
    List<Content> findByTopicId(Long topicId);
    List<Content> findByTopicIdAndContentType(Long topicId, Content.ContentType contentType);
}
