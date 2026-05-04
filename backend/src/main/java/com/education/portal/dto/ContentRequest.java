package com.education.portal.dto;

import com.education.portal.model.Content.ContentType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ContentRequest {

    @NotBlank
    @Size(min = 2, max = 200)
    private String title;

    @NotNull
    private ContentType contentType;

    private String body;

    @NotNull
    private Long topicId;
}
