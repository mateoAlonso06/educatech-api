package com.educatech.service;

import com.educatech.dto.request.CourseRequestDTO;
import com.educatech.dto.response.CourseResponseDTO;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ICourseService {
    CourseResponseDTO saveCourse(CourseRequestDTO course);

    Page<CourseResponseDTO> getAllCourses(Pageable pageable);

    CourseResponseDTO getCourseById(Long idCourse);

    CourseResponseDTO updateCourse(Long idCourse, CourseRequestDTO courseWithUpdates);

    void deleteCourse(Long idCourse);

    List<CourseResponseDTO> getCoursesByTeacher(Long idTeacher);

    List<CourseResponseDTO> searchCoursesByTitle(String titleKeyword);
}
