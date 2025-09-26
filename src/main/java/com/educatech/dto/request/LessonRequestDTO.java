package com.educatech.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class LessonRequestDTO {
    @NotBlank(message = "Titlte cannot be blank")
    @Size(max = 255, message = "Title cannot exceed 255 characters")
    private final String title;

    @NotBlank(message = "Content cannot be blank")
    @Size(max = 2000, message = "Content cannot exceed 2000 characters")
    private final String content;

    @NotNull(message = "Course ID cannot be null")
    private final Long courseId;
}
