package com.education.portal.dto;

import com.education.portal.model.Content;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ContentResponse {
    private Long id;
    private String title;
    private String contentType;
    private String body;
    private Long topicId;
    private String topicName;
    private LocalDateTime createdAt;

    public static ContentResponse from(Content c) {
        ContentResponse r = new ContentResponse();
        r.id = c.getId();
        r.title = c.getTitle();
        r.contentType = c.getContentType().name();
        r.body = c.getBody();
        r.topicId = c.getTopic().getId();
        r.topicName = c.getTopic().getName();
        r.createdAt = c.getCreatedAt();
        return r;
    }
}
