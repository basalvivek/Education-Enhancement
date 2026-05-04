package com.education.portal.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ClassRequest {

    @NotBlank
    @Size(min = 2, max = 100)
    private String name;

    private String description;

    @NotNull
    private Long categoryId;
}
