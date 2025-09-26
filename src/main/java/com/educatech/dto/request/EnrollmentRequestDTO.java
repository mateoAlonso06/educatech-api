package com.educatech.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class EnrollmentRequestDTO {
    @NotNull(message = "User ID cannot be null")
    private final Long userId;

    @NotNull(message = "Course ID cannot be null")
    private final Long courseId;
}
