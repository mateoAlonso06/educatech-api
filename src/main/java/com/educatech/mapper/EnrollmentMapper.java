package com.educatech.mapper;

import com.educatech.dto.request.EnrollmentRequestDTO;
import com.educatech.dto.response.EnrollmentResponseDTO;
import com.educatech.entity.Enrollment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface EnrollmentMapper {

    /**
     * Maps an Enrollment entity to an EnrollmentResponseDTO.
     * Maps the student's ID and course's ID from their respective nested objects.
     *
     * @param enrollment The Enrollment entity.
     * @return The mapped EnrollmentResponseDTO.
     */
    @Mapping(source = "student.id", target = "userId")
    @Mapping(source = "course.id", target = "courseId")
    EnrollmentResponseDTO toResponseDTO(Enrollment enrollment);

    /**
     * Maps an EnrollmentRequestDTO to an Enrollment entity.
     * 'id' is ignored as it's auto-generated.
     * 'student' and 'course' are ignored because they need to be fetched from the DB
     * in the service layer using userId and courseId from the DTO.
     * 'enrollmentDate' is ignored because it will be set automatically via @PrePersist.
     *
     * @param dto The EnrollmentRequestDTO.
     * @return The mapped Enrollment entity.
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "student", ignore = true)
    @Mapping(target = "course", ignore = true)
    @Mapping(target = "enrollmentDate", ignore = true)
    Enrollment toEntity(EnrollmentRequestDTO dto);
}
