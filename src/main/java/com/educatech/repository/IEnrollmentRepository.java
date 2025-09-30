package com.educatech.repository;

import com.educatech.entity.Course;
import com.educatech.entity.Enrollment;
import com.educatech.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IEnrollmentRepository extends JpaRepository<Enrollment, Long> {
    List<Enrollment> getEnrollmentsByCourse(Course course);

    Optional<Enrollment> getEnrollmentByStudentAndCourse(User student, Course course);

    List<Enrollment> getEnrollmentsByStudent(User student);

    User student(User student);
}