package com.educatech.service.impl;

import com.educatech.dto.request.EnrollmentRequestDTO;
import com.educatech.dto.response.EnrollmentResponseDTO;
import com.educatech.entity.Course;
import com.educatech.entity.Enrollment;
import com.educatech.entity.User;
import com.educatech.enums.Role;
import com.educatech.exception.EnrollmentNotFoundException;
import com.educatech.mapper.EnrollmentMapper;
import com.educatech.mapper.UserMapper;
import com.educatech.repository.ICourseRepository;
import com.educatech.repository.IEnrollmentRepository;
import com.educatech.repository.IUserRepository;
import com.educatech.service.IEnrollmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EnrollmentService implements IEnrollmentService {
    /** Repositorios y mapeadores necesarios para las operaciones de inscripción */
    private final IEnrollmentRepository enrollmentRepository;
    private final EnrollmentMapper enrollmentMapper;
    private final IUserRepository userRepository;
    private final ICourseRepository courseRepository;
    private final UserMapper userMapper;

    /**
     * Guarda una nueva inscripción.
     *
     * @param enrollment DTO de solicitud de inscripción
     * @return DTO de respuesta de la inscripción guardada
     */
    @Override
    public EnrollmentResponseDTO saveEnrollment(EnrollmentRequestDTO enrollment) {
        Enrollment enrollmentToSave = enrollmentMapper.toEntity(enrollment);
        Enrollment savedEnrollment = enrollmentRepository.save(enrollmentToSave);
        return enrollmentMapper.toResponseDTO(savedEnrollment);
    }

    /**
     * Obtiene todas las inscripciones con paginación.
     *
     * @param pageable Parámetros de paginación
     * @return Página de DTOs de respuesta de inscripciones
     */
    @Override
    public Page<EnrollmentResponseDTO> getAllEnrollments(Pageable pageable) {
        return enrollmentRepository.findAll(pageable).map(enrollmentMapper::toResponseDTO);
    }

    /**
     * Obtiene una inscripción por su ID.
     *
     * @param id ID de la inscripción
     * @return DTO de respuesta de la inscripción
     */
    @Override
    public EnrollmentResponseDTO getEnrollmentById(Long id) {
        Enrollment enrollment = this.getEnrollmentEntityById(id);
        return enrollmentMapper.toResponseDTO(enrollment);
    }

    /**
     * Actualiza una inscripción existente.
     *
     * @param idEnrollment          ID de la inscripción a actualizar
     * @param enrollmentWithUpdates DTO con los datos actualizados de la inscripción
     * @return La inscripción actualizada
     */
    @Override
    public Enrollment updateEnrollment(Long idEnrollment, EnrollmentRequestDTO enrollmentWithUpdates) {
        Enrollment existingEnrollment = this.getEnrollmentEntityById(idEnrollment);

        // Hay que asegurarse que el estudiante y el curso existen antes de actualizar
        User student = userRepository.findById(enrollmentWithUpdates.getUserId())
                .orElseThrow(() -> new EnrollmentNotFoundException("Student not found with id: " + enrollmentWithUpdates.getUserId()));

        Course course = courseRepository.findById(enrollmentWithUpdates.getCourseId())
                .orElseThrow(() -> new EnrollmentNotFoundException("Course not found with id: " + enrollmentWithUpdates.getCourseId()));

        return enrollmentRepository.save(existingEnrollment);
    }

    /**
     * Elimina una inscripción dado su ID.
     *
     * @param id ID de la inscripción a eliminar
     */
    @Override
    public void deleteEnrollment(Long id) {
        Enrollment enrollmentToDelete = this.getEnrollmentEntityById(id);
        enrollmentRepository.delete(enrollmentToDelete);
    }

    /**
     * Obtiene las inscripciones de un estudiante dado su ID.
     *
     * @param idStudent ID del estudiante
     * @return Lista de DTOs de respuesta de inscripciones
     */
    @Override
    public List<EnrollmentResponseDTO> getEnrollmentsByStudent(Long idStudent) {
        return null;
    }

    /**
     * Obtiene las inscripciones de un curso dado su ID.
     *
     * @param idCourse ID del curso
     * @return Lista de DTOs de respuesta de inscripciones
     */
    @Override
    public List<EnrollmentResponseDTO> getEnrollmentsByCourse(Long idCourse) {
        Course courseToFind = courseRepository.findById(idCourse)
                .orElseThrow(() -> new EnrollmentNotFoundException("Course not found with id: " + idCourse));

        return enrollmentRepository.getEnrollmentsByCourse(courseToFind).stream()
                .map(enrollmentMapper::toResponseDTO)
                .toList();
    }

    /**
     * Obtiene una inscripción dada la combinación de ID de estudiante y ID de curso.
     *
     * @param idStudent ID del estudiante
     * @param idCourse  ID del curso
     * @return DTO con la entidad de inscripción si existe, vacío en caso contrario
     */
    @Override
    public EnrollmentResponseDTO getEnrollmentByStudentAndCourse(Long idStudent, Long idCourse) {
        // primero que hay que verificar que el estudiante y el curso existen
        User studentToFind = userRepository.findById(idStudent)
                .orElseThrow(() -> new EnrollmentNotFoundException("Student not found with id: " + idStudent));

        if (!isRole(studentToFind, Role.STUDENT)) {
            throw new EnrollmentNotFoundException("User with id: " + idStudent + " is not a student");
        }

        Course courseToFind = courseRepository.findById(idCourse)
                .orElseThrow(() -> new EnrollmentNotFoundException("Course not found with id: " + idCourse));

        Enrollment enrollment = enrollmentRepository.getEnrollmentByStudentAndCourse(studentToFind, courseToFind);
        return enrollmentMapper.toResponseDTO(enrollment);
    }

    /**
     * Método auxiliar para obtener una entidad de inscripción por su ID.
     * Lanza una excepción si no se encuentra la inscripción.
     *
     * @param id ID de la inscripción
     * @return Entidad de inscripción
     */
    private Enrollment getEnrollmentEntityById(Long id) {
        return enrollmentRepository.findById(id)
                .orElseThrow(() -> new EnrollmentNotFoundException("Enrollment not found with id: " + id));
    }

    /**
     * Método auxiliar para verificar si un usuario tiene un rol específico.
     *
     * @param user Usuario a verificar
     * @param role Rol a comprobar
     * @return true si el usuario tiene el rol especificado, false en caso contrario
     */
    private boolean isRole(User user, Role role) {
        return user.getRole() == role;
    }
}
