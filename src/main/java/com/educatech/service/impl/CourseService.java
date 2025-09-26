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

    @Override
    public CourseResponseDTO saveCourse(CourseRequestDTO course) {
        Course courseToSave = courseMapper.toEntity(course);
        Course savedCourse = courseRepository.save(courseToSave);
        return courseMapper.toResponseDTO(savedCourse);
    }

    @Override
    public Page<CourseResponseDTO> getAllCourses(Pageable pageable) {
        return courseRepository.findAll(pageable).map(courseMapper::toResponseDTO);
    }

    @Override
    public CourseResponseDTO getCourseById(Long idCourse) {
        return courseMapper.toResponseDTO(this.getCourseEntityById(idCourse));
    }

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

    @Override
    public void deleteCourse(Long idCourse) {
        Course course = this.getCourseEntityById(idCourse);
        courseRepository.delete(course);
    }

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

    @Override
    public List<CourseResponseDTO> searchCoursesByTitle(String titleKeyword) {
        return courseRepository.findAllByTitleLike(titleKeyword)
                .stream()
                .map(courseMapper::toResponseDTO)
                .toList();
    }

    private Course getCourseEntityById(Long idCourse) {
        String message = "Course not found with id: " + idCourse;
        return courseRepository.findById(idCourse)
                .orElseThrow(() -> new CourseNotFoundException(message));
    }
}
