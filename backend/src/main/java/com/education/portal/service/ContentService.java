package com.education.portal.service;

import com.education.portal.dto.ContentRequest;
import com.education.portal.dto.ContentResponse;
import com.education.portal.model.Content;
import com.education.portal.model.Topic;
import com.education.portal.repository.ContentRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ContentService {

    private final ContentRepository contentRepository;
    private final TopicService topicService;

    public List<ContentResponse> getByTopicId(Long topicId) {
        return contentRepository.findByTopicId(topicId).stream()
                .map(ContentResponse::from)
                .toList();
    }

    public ContentResponse getById(Long id) {
        return ContentResponse.from(findOrThrow(id));
    }

    public ContentResponse create(ContentRequest request) {
        Topic topic = topicService.findOrThrow(request.getTopicId());

        Content content = Content.builder()
                .title(request.getTitle())
                .contentType(request.getContentType())
                .body(request.getBody())
                .topic(topic)
                .build();
        return ContentResponse.from(contentRepository.save(content));
    }

    public ContentResponse update(Long id, ContentRequest request) {
        Content content = findOrThrow(id);
        content.setTitle(request.getTitle());
        content.setContentType(request.getContentType());
        content.setBody(request.getBody());
        return ContentResponse.from(contentRepository.save(content));
    }

    public void delete(Long id) {
        findOrThrow(id);
        contentRepository.deleteById(id);
    }

    public Content findOrThrow(Long id) {
        return contentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Content not found: " + id));
    }
}
