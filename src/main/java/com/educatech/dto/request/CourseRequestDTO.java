package com.educatech.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class CourseRequestDTO {
    @NotBlank(message = "Title cannot be blank")
    @Size(max = 255, message = "Title cannot exceed 255 characters")
    private final String title;

    @NotBlank(message = "Description cannot be blank")
    @Size(max = 10000, message = "Description cannot exceed 10.000 characters")
    private final String description;

    @NotNull(message = "Teacher ID cannot be null")
    @Positive
    private final Long teacherId;
}
