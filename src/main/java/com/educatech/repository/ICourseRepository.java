package com.educatech.repository;

import com.educatech.entity.Course;
import com.educatech.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ICourseRepository extends JpaRepository<Course, Long> {
    List<Course> findAllByTeacher(User teacher);

    List<Course> findAllByTitleLike(String title);
}
