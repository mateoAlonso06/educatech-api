package com.educatech.service.impl;

import com.educatech.dto.request.EnrollmentRequestDTO;
import com.educatech.dto.response.EnrollmentResponseDTO;
import com.educatech.entity.Course;
import com.educatech.entity.Enrollment;
import com.educatech.entity.User;
import com.educatech.enums.Role;
import com.educatech.exception.CourseNotFoundException;
import com.educatech.exception.EnrollmentNotFoundException;
import com.educatech.exception.StudentHasEnrolledException;
import com.educatech.exception.UserNotFoundException;
import com.educatech.mapper.EnrollmentMapper;
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

    /**
     * Guarda una nueva inscripción.
     *
     * @param enrollment DTO de solicitud de inscripción
     * @return DTO de respuesta de la inscripción guardada
     */
    @Override
    public EnrollmentResponseDTO saveEnrollment(EnrollmentRequestDTO enrollment) {
        Long idStudent = enrollment.getUserId();
        Long idCourse = enrollment.getCourseId();

        // Validar que el ID del estudiante y del curso sean válidos
        if (idStudent == null || idStudent <= 0) {
            throw new IllegalArgumentException("Invalid student ID: " + idStudent);
        }

        if (idCourse == null || idCourse <= 0) {
            throw new IllegalArgumentException("Invalid course ID: " + idCourse);
        }

        User student = userRepository.findById(idStudent)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + idStudent));

        if (!this.hasRole(student, Role.STUDENT)) {
            throw new IllegalArgumentException("User with id: " + idStudent + " is not a student");
        }

        Course course = courseRepository.findById(idCourse)
                .orElseThrow(() -> new CourseNotFoundException("Course not found with id: " + idCourse));

        enrollmentRepository.getEnrollmentByStudentAndCourse(student, course)
                .ifPresent(enrollment1 -> {
                    throw new StudentHasEnrolledException("Student with id: " + idStudent + " has already enrolled in course with id: " + idCourse);
                });

        Enrollment enrollmentToSave = enrollmentMapper.toEntity(enrollment);

        enrollmentToSave.setStudent(student);
        enrollmentToSave.setCourse(course);

        Enrollment savedEnrollment = enrollmentRepository.save(enrollmentToSave);
        // solo devuelve los ids del estudiante y del curso
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
        // el metodo privado maneja la excepcion
        return enrollmentMapper.toResponseDTO(this.getEnrollmentEntityById(id));
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
        if (idEnrollment == null || idEnrollment <= 0) {
            throw new IllegalArgumentException("Invalid enrollment ID: " + idEnrollment);
        }

        if (enrollmentWithUpdates.getUserId() == null || enrollmentWithUpdates.getUserId() <= 0) {
            throw new IllegalArgumentException("Invalid student ID: " + enrollmentWithUpdates.getUserId());
        }

        if (enrollmentWithUpdates.getCourseId() == null || enrollmentWithUpdates.getCourseId() <= 0) {
            throw new IllegalArgumentException("Invalid course ID: " + enrollmentWithUpdates.getCourseId());
        }

        Enrollment existingEnrollment = this.getEnrollmentEntityById(idEnrollment);

        // Hay que asegurarse que el estudiante y el curso existen antes de actualizar
        User student = userRepository.findById(enrollmentWithUpdates.getUserId())
                .orElseThrow(() -> new EnrollmentNotFoundException("Student not found with id: " + enrollmentWithUpdates.getUserId()));

        if (!this.hasRole(student, Role.STUDENT)) {
            throw new IllegalArgumentException("User with id: " + enrollmentWithUpdates.getUserId() + " is not a student");
        }

        Course course = courseRepository.findById(enrollmentWithUpdates.getCourseId())
                .orElseThrow(() -> new CourseNotFoundException("Course not found with id: " + enrollmentWithUpdates.getCourseId()));

        return enrollmentRepository.save(existingEnrollment);
    }

    /**
     * Elimina una inscripción dado su ID.
     *
     * @param enrollmentId ID de la inscripción a eliminar
     */
    @Override
    public void deleteEnrollment(Long enrollmentId) {
        if (enrollmentId == null || enrollmentId <= 0) {
            throw new IllegalArgumentException("Enrollment with id: " + enrollmentId + " does not exist");
        }

        Enrollment enrollmentToDelete = this.getEnrollmentEntityById(enrollmentId);
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
        if (idStudent == null || idStudent <= 0) {
            throw new IllegalArgumentException("Invalid student ID: " + idStudent);
        }

        User studentToFind = userRepository.findById(idStudent)
                .orElseThrow(() -> new UserNotFoundException("Student not found with id: " + idStudent));

        if (!this.hasRole(studentToFind, Role.STUDENT)) {
            throw new IllegalArgumentException("User with id: " + idStudent + " is not a student");
        }

        return enrollmentRepository.getEnrollmentsByStudent(studentToFind)
                .stream().map(enrollmentMapper::toResponseDTO)
                .toList();
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
                .orElseThrow(() -> new CourseNotFoundException("Course not found with id: " + idCourse));

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
        User studentToFind = userRepository.findById(idStudent)
                .orElseThrow(() -> new UserNotFoundException("Student not found with id: " + idStudent));

        if (!hasRole(studentToFind, Role.STUDENT)) {
            throw new IllegalArgumentException("User with id: " + idStudent + " is not a student");
        }

        Course courseToFind = courseRepository.findById(idCourse)
                .orElseThrow(() -> new CourseNotFoundException("Course not found with id: " + idCourse));

        Enrollment enrollment = enrollmentRepository.getEnrollmentByStudentAndCourse(studentToFind, courseToFind)
                .orElseThrow(() -> new EnrollmentNotFoundException("Enrollment not found for student id: " + idStudent + " and course id: " + idCourse));

        return enrollmentMapper.toResponseDTO(enrollment);
    }

    // Metodo auxiliar para obtener una entidad de inscripción por su ID, lanzando una excepción si no se encuentra
    private Enrollment getEnrollmentEntityById(Long enrollmentId) {
        return enrollmentRepository.findById(enrollmentId)
                .orElseThrow(() -> new EnrollmentNotFoundException("Enrollment not found with id: " + enrollmentId));
    }

    // Metodo auxiliar para comprobar el rol de un usuario
    private boolean hasRole(User user, Role role) {
        return user.getRole() == role;
    }
}
