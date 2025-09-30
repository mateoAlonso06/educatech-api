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
import org.springframework.transaction.annotation.Transactional;

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
    @Transactional
    public LessonResponseDTO saveLesson(LessonRequestDTO lesson) {
        if (lesson.getCourseId() == null || lesson.getCourseId() <= 0) {
            throw new IllegalArgumentException("Course ID must be provided and greater than zero.");
        }
        Course course = courseRepository.findById(lesson.getCourseId())
                .orElseThrow(() -> new CourseNotFoundException("Course not found with id: " + lesson.getCourseId()));

        lessonRepository.getLessonByTitleAndCourse(lesson.getTitle(), course).ifPresent(
                l -> {
                    throw new IllegalArgumentException("A lesson with the same title already exists in this course.");
                }
        );

        Lesson lessonToSave = lessonMapper.toEntity(lesson);
        lessonToSave.setContent(lesson.getContent());
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
    @Transactional(readOnly = true)
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
    @Transactional(readOnly = true)
    public LessonResponseDTO getLessonById(Long idLesson) {
        if (idLesson == null || idLesson <= 0) {
            throw new IllegalArgumentException("Lesson ID must be provided and greater than zero.");
        }
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
    @Transactional
    public LessonResponseDTO updateLesson(Long idLesson, LessonRequestDTO lessonWithUpdates) {
        if (idLesson == null || idLesson <= 0) {
            throw new IllegalArgumentException("Lesson ID must be provided and greater than zero.");
        }
        if (lessonWithUpdates.getCourseId() == null || lessonWithUpdates.getCourseId() <= 0) {
            throw new IllegalArgumentException("Course ID must be provided and greater than zero.");
        }

        Course course = courseRepository.findById(lessonWithUpdates.getCourseId())
                .orElseThrow(() -> new CourseNotFoundException("Course not found with id: " + lessonWithUpdates.getCourseId()));

        lessonRepository.getLessonByTitleAndCourse(lessonWithUpdates.getTitle(), course)
                .ifPresent(lesson -> {
                    if (!lesson.getId().equals(idLesson)) {
                        throw new IllegalArgumentException("A lesson with the same title already exists in this course.");
                    }
                });

        // verifica si la lección existe
        Lesson existingLesson = this.getLessonEntityById(idLesson);

        // EL problema de este metodo es que si quiero actualizar el NOMBRE y el CONTENIDO de la lección
        // y NO el curso, me da error porque la lección ya está asociada a ese curso.
        // Lo que se podría hacer es permitir que no se actualice el curso si no se quiere cambiar.
        // lo mejor es darlo para un PUT completo, manejarlo con un PATCH para actualizaciones parciales

//        if (existingLesson.getCourse().getId().equals(course.getId())) {
//            throw new IllegalArgumentException("The lesson is already associated with the specified course.");
//        }

        existingLesson.setTitle(lessonWithUpdates.getTitle());
        existingLesson.setContent(lessonWithUpdates.getContent());
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
    @Transactional
    public void deleteLesson(Long idLesson) {
        if (idLesson == null || idLesson <= 0) {
            throw new IllegalArgumentException("Lesson ID must be provided and greater than zero.");
        }
        // Verifica si la lección existe
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
    @Transactional(readOnly = true)
    public List<LessonResponseDTO> getLessonsByCourse(Long idCourse) {
        if (idCourse == null || idCourse <= 0) {
            throw new IllegalArgumentException("Course ID must be provided and greater than zero.");
        }
        Course course = courseRepository.findById(idCourse)
                .orElseThrow(() -> new CourseNotFoundException("Course not found with id: " + idCourse));

        return lessonRepository.getLessonsByCourse(course)
                .stream()
                .map(lessonMapper::toResponseDTO)
                .toList();
    }

    // Método privado para obtener una lección por su ID o lanzar una excepción si no existe
    private Lesson getLessonEntityById(Long idLesson) {
        return lessonRepository.findById(idLesson)
                .orElseThrow(() -> new LessonNotFoundException("Lesson not found with id: " + idLesson));
    }
}
