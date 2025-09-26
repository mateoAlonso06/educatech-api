package com.educatech.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@AllArgsConstructor
@Getter
public class EnrollmentResponseDTO {
    private final Long id;
    private final Long userId;
    private final Long courseId;
    private final LocalDateTime enrollmentDate;
}
