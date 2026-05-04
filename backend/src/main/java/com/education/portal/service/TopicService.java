package com.education.portal.service;

import com.education.portal.dto.TopicRequest;
import com.education.portal.dto.TopicResponse;
import com.education.portal.model.Class;
import com.education.portal.model.Topic;
import com.education.portal.repository.ClassRepository;
import com.education.portal.repository.TopicRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TopicService {

    private final TopicRepository topicRepository;
    private final ClassRepository classRepository;

    public List<TopicResponse> getByClassId(Long classId) {
        return topicRepository.findByAClassId(classId).stream()
                .map(TopicResponse::from)
                .toList();
    }

    public TopicResponse getById(Long id) {
        return TopicResponse.from(findOrThrow(id));
    }

    public TopicResponse create(TopicRequest request) {
        Class aClass = classRepository.findById(request.getClassId())
                .orElseThrow(() -> new EntityNotFoundException("Class not found: " + request.getClassId()));

        if (topicRepository.existsByNameIgnoreCaseAndAClassId(request.getName(), request.getClassId())) {
            throw new IllegalArgumentException("Topic already exists in this class: " + request.getName());
        }

        Topic topic = Topic.builder()
                .name(request.getName())
                .description(request.getDescription())
                .aClass(aClass)
                .build();
        return TopicResponse.from(topicRepository.save(topic));
    }

    public TopicResponse update(Long id, TopicRequest request) {
        Topic topic = findOrThrow(id);
        topic.setName(request.getName());
        topic.setDescription(request.getDescription());
        return TopicResponse.from(topicRepository.save(topic));
    }

    public void delete(Long id) {
        findOrThrow(id);
        topicRepository.deleteById(id);
    }

    public Topic findOrThrow(Long id) {
        return topicRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Topic not found: " + id));
    }
}
