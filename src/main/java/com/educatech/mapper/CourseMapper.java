package com.educatech.mapper;

import com.educatech.dto.request.CourseRequestDTO;
import com.educatech.dto.response.CourseResponseDTO;
import com.educatech.entity.Course;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CourseMapper {

    /**
     * Maps a Course entity to a CourseResponseDTO.
     * The teacher's ID is extracted from the nested User object.
     *
     * @param course The Course entity.
     * @return The mapped CourseResponseDTO.
     */
    @Mapping(source = "teacher.id", target = "teacherId")
    CourseResponseDTO toResponseDTO(Course course);

    /**
     * Maps a CourseRequestDTO to a Course entity.
     * 'id' is ignored as it's auto-generated.
     * 'teacher' is ignored because it needs to be fetched from the DB
     * in the service layer using the teacherId from the DTO.
     *
     * @param dto The CourseRequestDTO.
     * @return The mapped Course entity.
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "teacher", ignore = true)
    Course toEntity(CourseRequestDTO dto);
}
