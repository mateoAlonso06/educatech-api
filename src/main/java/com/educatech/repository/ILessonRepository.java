package com.educatech.repository;

import com.educatech.entity.Lesson;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ILessonRepository extends JpaRepository<Lesson, Long> {
}
