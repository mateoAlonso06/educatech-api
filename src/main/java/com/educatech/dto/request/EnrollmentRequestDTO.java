package com.educatech.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class EnrollmentRequestDTO {
    @NotNull(message = "User ID cannot be null")
    @Positive
    private final Long userId;

    @NotNull(message = "Course ID cannot be null")
    @Positive
    private final Long courseId;
}
