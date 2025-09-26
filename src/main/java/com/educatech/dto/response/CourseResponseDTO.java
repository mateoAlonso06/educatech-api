package com.educatech.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class CourseResponseDTO {
    private final Long id;
    private final String title;
    private final String description;
    private final Long teacherId;
}
