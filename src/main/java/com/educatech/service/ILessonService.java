package com.educatech.service;

import com.educatech.dto.request.LessonRequestDTO;
import com.educatech.dto.response.LessonResponseDTO;
import com.educatech.entity.Lesson;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface ILessonService {
    LessonResponseDTO saveLesson(LessonRequestDTO lesson);

    Page<LessonResponseDTO> getAllLessons(Pageable pageable);

    Optional<Lesson> getLessonById(Long idLesson);

    Lesson updateLesson(Long idLesson, LessonRequestDTO lessonWithUpdates);

    void deleteLesson(Long idLesson);

    List<LessonResponseDTO> getLessonsByCourse(Long idCourse);
}
