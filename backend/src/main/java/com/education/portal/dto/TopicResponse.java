package com.education.portal.dto;

import com.education.portal.model.Topic;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class TopicResponse {
    private Long id;
    private String name;
    private String description;
    private Long classId;
    private String className;
    private LocalDateTime createdAt;

    public static TopicResponse from(Topic t) {
        TopicResponse r = new TopicResponse();
        r.id = t.getId();
        r.name = t.getName();
        r.description = t.getDescription();
        r.classId = t.getAClass().getId();
        r.className = t.getAClass().getName();
        r.createdAt = t.getCreatedAt();
        return r;
    }
}
