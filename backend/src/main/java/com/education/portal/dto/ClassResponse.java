package com.education.portal.dto;

import com.education.portal.model.Class;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ClassResponse {
    private Long id;
    private String name;
    private String description;
    private Long categoryId;
    private String categoryName;
    private LocalDateTime createdAt;

    public static ClassResponse from(Class c) {
        ClassResponse r = new ClassResponse();
        r.id = c.getId();
        r.name = c.getName();
        r.description = c.getDescription();
        r.categoryId = c.getCategory().getId();
        r.categoryName = c.getCategory().getName();
        r.createdAt = c.getCreatedAt();
        return r;
    }
}
