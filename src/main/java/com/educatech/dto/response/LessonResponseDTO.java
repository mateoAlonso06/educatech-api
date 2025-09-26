package com.educatech.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class LessonResponseDTO {
    private final String title;
    private final String content;
    private final Long courseId;
}
