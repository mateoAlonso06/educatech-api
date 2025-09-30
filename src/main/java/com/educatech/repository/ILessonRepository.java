package com.educatech.repository;

import com.educatech.entity.Course;
import com.educatech.entity.Lesson;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ILessonRepository extends JpaRepository<Lesson, Long> {
    List<Lesson> getLessonsByCourse(Course course);

    Optional<Lesson> getLessonByTitleAndCourse(String title, Course course);
}
