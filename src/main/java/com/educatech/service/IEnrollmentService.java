package com.educatech.service;

import com.educatech.dto.request.EnrollmentRequestDTO;
import com.educatech.dto.response.EnrollmentResponseDTO;
import com.educatech.entity.Course;
import com.educatech.entity.Enrollment;
import com.educatech.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface IEnrollmentService {
    EnrollmentResponseDTO saveEnrollment(EnrollmentRequestDTO enrollment);

    Page<EnrollmentResponseDTO> getAllEnrollments(Pageable pageable);

    EnrollmentResponseDTO getEnrollmentById(Long id);

    Enrollment updateEnrollment(Long idEnrollment, EnrollmentRequestDTO enrollmentWithUpdates);

    void deleteEnrollment(Long id);

    List<EnrollmentResponseDTO> getEnrollmentsByStudent(Long idStudent);

    List<EnrollmentResponseDTO> getEnrollmentsByCourse(Long idCourse);

    EnrollmentResponseDTO getEnrollmentByStudentAndCourse(Long idStudent, Long idCourse);
}
