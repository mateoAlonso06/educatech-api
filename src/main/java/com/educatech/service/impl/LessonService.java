package com.educatech.service.impl;

import com.educatech.dto.request.LessonRequestDTO;
import com.educatech.dto.response.LessonResponseDTO;
import com.educatech.entity.Course;
import com.educatech.entity.Lesson;
import com.educatech.exception.CourseNotFoundException;
import com.educatech.exception.LessonNotFoundException;
import com.educatech.mapper.LessonMapper;
import com.educatech.repository.ICourseRepository;
import com.educatech.repository.ILessonRepository;
import com.educatech.service.ILessonService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LessonService implements ILessonService {
    private final ILessonRepository lessonRepository;
    private final ICourseRepository courseRepository;
    private final LessonMapper lessonMapper;

    /**
     * Guarda una nueva lección.
     *
     * @param lesson DTO de solicitud de lección.
     * @return DTO de respuesta de la lección guardada.
     */
    @Override
    public LessonResponseDTO saveLesson(LessonRequestDTO lesson) {
        Lesson lessonToSave = lessonMapper.toEntity(lesson);
        Lesson savedLesson = lessonRepository.save(lessonToSave);
        return lessonMapper.toResponseDTO(savedLesson);
    }

    /**
     * Obtiene todas las lecciones con paginación.
     *
     * @param pageable Parámetros de paginación.
     * @return Página de DTOs de respuesta de lecciones.
     */
    @Override
    public Page<LessonResponseDTO> getAllLessons(Pageable pageable) {
        return lessonRepository.findAll(pageable).map(lessonMapper::toResponseDTO);
    }

    /**
     * Obtiene una lección por su ID.
     *
     * @param idLesson ID de la lección.
     * @return DTO de respuesta de la lección.
     */
    @Override
    public LessonResponseDTO getLessonById(Long idLesson) {
        return lessonMapper.toResponseDTO(this.getLessonEntityById(idLesson));
    }

    /**
     * Actualiza una lección existente.
     *
     * @param idLesson          ID de la lección a actualizar.
     * @param lessonWithUpdates DTO con los datos actualizados de la lección.
     * @return DTO de respuesta de la lección actualizada.
     */
    @Override
    public LessonResponseDTO updateLesson(Long idLesson, LessonRequestDTO lessonWithUpdates) {
        Lesson existingLesson = this.getLessonEntityById(idLesson);

        existingLesson.setTitle(lessonWithUpdates.getTitle());
        existingLesson.setContent(lessonWithUpdates.getContent());

        Course course = courseRepository.findById(lessonWithUpdates.getCourseId())
                .orElseThrow(() -> new LessonNotFoundException("Course not found with id: " + lessonWithUpdates.getCourseId()));

        existingLesson.setCourse(course);

        Lesson updatedLesson = lessonRepository.save(existingLesson);
        return lessonMapper.toResponseDTO(updatedLesson);
    }

    /**
     * Elimina una lección dado su ID.
     *
     * @param idLesson ID de la lección a eliminar.
     */
    @Override
    public void deleteLesson(Long idLesson) {
        Lesson lessonToDelete = this.getLessonEntityById(idLesson);
        lessonRepository.delete(lessonToDelete);
    }

    /**
     * Obtiene las lecciones de un curso dado su ID.
     *
     * @param idCourse ID del curso.
     * @return Lista de DTOs de respuesta de lecciones.
     */
    @Override
    public List<LessonResponseDTO> getLessonsByCourse(Long idCourse) {
        Course course = courseRepository.findById(idCourse)
                .orElseThrow(() -> new CourseNotFoundException("Course not found with id: " + idCourse));

        return lessonRepository.getLessonsByCourse(course)
                .stream()
                .map(lessonMapper::toResponseDTO)
                .toList();
    }

    /**
     * Método auxiliar para obtener una entidad de lección por su ID.
     * Lanza una excepción si no se encuentra.
     *
     * @param idLesson ID de la lección.
     * @return Entidad de lección.
     */
    private Lesson getLessonEntityById(Long idLesson) {
        return lessonRepository.findById(idLesson)
                .orElseThrow(() -> new LessonNotFoundException("Lesson not found with id: " + idLesson));
    }
}
