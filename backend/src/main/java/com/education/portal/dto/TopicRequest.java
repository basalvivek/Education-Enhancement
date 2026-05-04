package com.education.portal.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class TopicRequest {

    @NotBlank
    @Size(min = 2, max = 150)
    private String name;

    private String description;

    @NotNull
    private Long classId;
}
