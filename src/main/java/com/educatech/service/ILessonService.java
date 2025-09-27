package com.educatech.service;

import com.educatech.dto.request.LessonRequestDTO;
import com.educatech.dto.response.LessonResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ILessonService {
    LessonResponseDTO saveLesson(LessonRequestDTO lesson);

    Page<LessonResponseDTO> getAllLessons(Pageable pageable);

    LessonResponseDTO getLessonById(Long idLesson);

    LessonResponseDTO updateLesson(Long idLesson, LessonRequestDTO lessonWithUpdates);

    void deleteLesson(Long idLesson);

    List<LessonResponseDTO> getLessonsByCourse(Long idCourse);
}
