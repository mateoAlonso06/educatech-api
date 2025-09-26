package com.educatech.mapper;

import com.educatech.dto.request.LessonRequestDTO;
import com.educatech.dto.response.LessonResponseDTO;
import com.educatech.entity.Lesson;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface LessonMapper {

    /**
     * Maps a Lesson entity to a LessonResponseDTO.
     * The course's ID is extracted from the nested Course object.
     *
     * @param lesson The Lesson entity.
     * @return The mapped LessonResponseDTO.
     */
    @Mapping(source = "course.id", target = "courseId")
    LessonResponseDTO toResponseDTO(Lesson lesson);

    /**
     * Maps a LessonRequestDTO to a Lesson entity.
     * 'id' is ignored as it's auto-generated.
     * 'course' is ignored because it needs to be fetched from the DB
     * in the service layer using the courseId from the DTO.
     *
     * @param dto The LessonRequestDTO.
     * @return The mapped Lesson entity.
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "course", ignore = true)
    Lesson toEntity(LessonRequestDTO dto);
}
