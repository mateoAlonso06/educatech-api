package com.educatech.repository;

import com.educatech.entity.Enrollment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IEnrollmentRepository extends JpaRepository<Enrollment, Long> {
}
