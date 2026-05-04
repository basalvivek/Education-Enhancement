package com.education.portal.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ExamStartRequest {

    @NotNull
    private Long contentId;
}
