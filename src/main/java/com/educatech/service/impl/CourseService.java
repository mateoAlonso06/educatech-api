package com.educatech.service.impl;

import com.educatech.dto.request.CourseRequestDTO;
import com.educatech.dto.response.CourseResponseDTO;
import com.educatech.entity.Course;
import com.educatech.entity.User;
import com.educatech.enums.Role;
import com.educatech.exception.CourseNotFoundException;
import com.educatech.exception.TeacherNotFoundException;
import com.educatech.exception.TeacherRoleRequiredException;
import com.educatech.mapper.CourseMapper;
import com.educatech.repository.ICourseRepository;
import com.educatech.repository.IUserRepository;
import com.educatech.service.ICourseService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

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
    public CourseResponseDTO saveCourse(CourseRequestDTO course) {
        Course courseToSave = courseMapper.toEntity(course);
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
    public CourseResponseDTO updateCourse(Long idCourse, CourseRequestDTO courseWithUpdates) {
        Course existingCourse = this.getCourseEntityById(idCourse);

        existingCourse.setTitle(courseWithUpdates.getTitle());
        existingCourse.setDescription(courseWithUpdates.getDescription());

        User teacher = userRepository.findById(courseWithUpdates.getTeacherId())
                .orElseThrow(() -> new TeacherNotFoundException("User not found with id: " + courseWithUpdates.getTeacherId()));

        if (!Role.TEACHER.equals(teacher.getRole())) {
            throw new TeacherRoleRequiredException("User with id: " + courseWithUpdates.getTeacherId() + " does not have the TEACHER role");
        }

        existingCourse.setTeacher(teacher);
        return courseMapper.toResponseDTO(courseRepository.save(existingCourse));
    }

    /**
     * Elimina un curso dado su ID.
     *
     * @param idCourse ID del curso a eliminar.
     */
    @Override
    public void deleteCourse(Long idCourse) {
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
    public List<CourseResponseDTO> getCoursesByTeacher(Long idTeacher) {
        User teacher = userRepository.findById(idTeacher)
                .orElseThrow(() -> new TeacherNotFoundException("Teacher not found with id: " + idTeacher)); // TODO: make custom exception

        if (!Role.TEACHER.equals(teacher.getRole())) {
            throw new TeacherRoleRequiredException("User with id: " + idTeacher + " is not a teacher");
        }

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
    public List<CourseResponseDTO> searchCoursesByTitle(String titleKeyword) {
        return courseRepository.findAllByTitleLike(titleKeyword)
                .stream()
                .map(courseMapper::toResponseDTO)
                .toList();
    }

    /**
     * Método auxiliar para obtener una entidad de curso por su ID.
     * Lanza una excepción si no se encuentra.
     *
     * @param idCourse ID del curso.
     * @return Entidad de curso.
     */
    private Course getCourseEntityById(Long idCourse) {
        String message = "Course not found with id: " + idCourse;
        return courseRepository.findById(idCourse)
                .orElseThrow(() -> new CourseNotFoundException(message));
    }
}
