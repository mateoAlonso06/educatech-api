package com.educatech.service.impl;

import com.educatech.dto.request.LessonRequestDTO;
import com.educatech.dto.response.LessonResponseDTO;
import com.educatech.entity.Lesson;
import com.educatech.mapper.LessonMapper;
import com.educatech.repository.ICourseRepository;
import com.educatech.repository.ILessonRepository;
import com.educatech.service.ILessonService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LessonService implements ILessonService {
    private final ILessonRepository lessonRepository;
    private final ICourseRepository courseRepository;
    private final LessonMapper lessonMapper;

    @Override
    public LessonResponseDTO saveLesson(LessonRequestDTO lesson) {
        Lesson lessonToSave = lessonMapper.toEntity(lesson);
        Lesson savedLesson = lessonRepository.save(lessonToSave);
        return lessonMapper.toResponseDTO(savedLesson);
    }

    @Override
    public Page<LessonResponseDTO> getAllLessons(Pageable pageable) {
        return lessonRepository.findAll(pageable).map(lessonMapper::toResponseDTO);
    }

    @Override
    public Optional<Lesson> getLessonById(Long idLesson) {
        return lessonRepository.findById(idLesson).orElseThrow(() -> new LessonNotFoundException("Lesson not found with id: " + idLesson));
    }

    @Override
    public Lesson updateLesson(Long idLesson, LessonRequestDTO lessonWithUpdates) {
        return null;
    }

    @Override
    public void deleteLesson(Long idLesson) {

    }

    @Override
    public List<LessonResponseDTO> getLessonsByCourse(Long idCourse) {
        return List.of();
    }
}
