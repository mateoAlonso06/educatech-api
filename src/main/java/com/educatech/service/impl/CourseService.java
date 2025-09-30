package com.educatech.service.impl;

import com.educatech.dto.request.CourseRequestDTO;
import com.educatech.dto.response.CourseResponseDTO;
import com.educatech.entity.Course;
import com.educatech.entity.User;
import com.educatech.enums.Role;
import com.educatech.exception.CourseNotFoundException;
import com.educatech.exception.UserNotFoundException;
import com.educatech.mapper.CourseMapper;
import com.educatech.repository.ICourseRepository;
import com.educatech.repository.IUserRepository;
import com.educatech.service.ICourseService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CourseService implements ICourseService {
    private final ICourseRepository courseRepository;
    private final IUserRepository userRepository;
    private final CourseMapper courseMapper;

     /**
     * Guarda un nuevo curso.
     *
     * @param course DTO de solicitud de curso.
     * @return DTO de respuesta del curso guardado.
     */
    @Override
    @Transactional
    public CourseResponseDTO saveCourse(CourseRequestDTO course) {
        if (course.getTeacherId() == null || course.getTeacherId() <= 0) {
            throw new IllegalArgumentException("Teacher ID cannot be null or less than 1");
        }

        User teacher = userRepository.findById(course.getTeacherId())
                .orElseThrow(() -> new UserNotFoundException("Teacher not found with id: " + course.getTeacherId()));

        if (!Role.TEACHER.equals(teacher.getRole())) {
            throw new IllegalArgumentException("User with id: " + course.getTeacherId() + " does not have the TEACHER role");
        }

        Course courseToSave = courseMapper.toEntity(course);
        courseToSave.setTeacher(teacher);
        Course savedCourse = courseRepository.save(courseToSave);
        return courseMapper.toResponseDTO(savedCourse);
    }

    /**
     * Obtiene todos los cursos con paginación.
     *
     * @param pageable Parámetros de paginación.
     * @return Página de DTOs de respuesta de cursos.
     */
    @Override
    @Transactional(readOnly = true)
    public Page<CourseResponseDTO> getAllCourses(Pageable pageable) {
        return courseRepository.findAll(pageable).map(courseMapper::toResponseDTO);
    }

    /**
     * Obtiene un curso por su ID.
     *
     * @param idCourse ID del curso.
     * @return DTO de respuesta del curso.
     */
    @Override
    @Transactional(readOnly = true)
    public CourseResponseDTO getCourseById(Long idCourse) {
        return courseMapper.toResponseDTO(this.getCourseEntityById(idCourse));
    }

    /**
     * Actualiza un curso existente.
     *
     * @param idCourse            ID del curso a actualizar.
     * @param courseWithUpdates DTO con los datos actualizados del curso.
     * @return DTO de respuesta del curso actualizado.
     */
    @Override
    @Transactional
    public CourseResponseDTO updateCourse(Long idCourse, CourseRequestDTO courseWithUpdates) {
        if (idCourse == null || idCourse <= 0) {
            throw new IllegalArgumentException("Course ID cannot be null or less than 1");
        }

        if (courseWithUpdates.getTeacherId() == null || courseWithUpdates.getTeacherId() <= 0) {
            throw new IllegalArgumentException("Teacher ID cannot be null or less than 1");
        }

        User teacher = userRepository.findById(courseWithUpdates.getTeacherId())
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + courseWithUpdates.getTeacherId()));

        if (!Role.TEACHER.equals(teacher.getRole())) {
            throw new IllegalArgumentException("User with id: " + courseWithUpdates.getTeacherId() + " does not have the TEACHER role");
        }
        // comprueba que el curso exista antes de actualizarlo
        Course existingCourse = this.getCourseEntityById(idCourse);

        if (existingCourse.getTeacher().getId().equals(teacher.getId())) {
            throw new IllegalArgumentException("The teacher is already assigned to this course");
        }

        existingCourse.setTitle(courseWithUpdates.getTitle());
        existingCourse.setDescription(courseWithUpdates.getDescription());
        existingCourse.setTeacher(teacher);
        return courseMapper.toResponseDTO(courseRepository.save(existingCourse));
    }

    /**
     * Elimina un curso dado su ID.
     *
     * @param idCourse ID del curso a eliminar.
     */
    @Override
    @Transactional
    public void deleteCourse(Long idCourse) {
        if (idCourse == null || idCourse <= 0) {
            throw new IllegalArgumentException("Course ID cannot be null or less than 1");
        }

        // comprueba que el curso exista antes de eliminarlo
        Course courseToDelete = this.getCourseEntityById(idCourse);
        courseRepository.delete(courseToDelete);
    }

    /**
     * Obtiene los cursos de un profesor dado su ID.
     *
     * @param idTeacher ID del profesor.
     * @return Lista de DTOs de respuesta de cursos.
     */
    @Override
    @Transactional(readOnly = true)
    public List<CourseResponseDTO> getCoursesByTeacher(Long idTeacher) {
        if (idTeacher == null || idTeacher <= 0) {
            throw new IllegalArgumentException("Teacher ID cannot be null or less than 1");
        }

        User teacher = userRepository.findById(idTeacher)
                .orElseThrow(() -> new UserNotFoundException("Teacher not found with id: " + idTeacher)); // TODO: make custom exception

        if (!Role.TEACHER.equals(teacher.getRole())) {
            throw new IllegalArgumentException("User with id: " + idTeacher + " is not a teacher");
        }

        // Si el profesor no tiene cursos, se devuelve una lista vacía
        return courseRepository.findAllByTeacher(teacher)
                .stream()
                .map(courseMapper::toResponseDTO)
                .toList();
    }

    /**
     * Busca cursos por una palabra clave en el título.
     *
     * @param titleKeyword Palabra clave para buscar en el título del curso.
     * @return Lista de DTOs de respuesta de los cursos encontrados.
     */
    @Override
    @Transactional(readOnly = true)
    public List<CourseResponseDTO> searchCoursesByTitle(String titleKeyword) {
        return courseRepository.findAllByTitleLike(titleKeyword)
                .stream()
                .map(courseMapper::toResponseDTO)
                .toList();
    }

    // Metodo auxiliar para manejar la excepcion de curso no encontrado
    private Course getCourseEntityById(Long idCourse) {
        String message = "Course not found with id: " + idCourse;
        return courseRepository.findById(idCourse)
                .orElseThrow(() -> new CourseNotFoundException(message));
    }
}
