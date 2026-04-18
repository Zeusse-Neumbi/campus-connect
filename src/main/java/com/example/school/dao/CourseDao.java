package com.example.school.dao;

import com.example.school.model.Course;
import java.util.List;
import java.util.Optional;

public interface CourseDao {
    void save(Course course);

    void update(Course course);

    void delete(int id);

    Optional<Course> findById(int id);

    List<Course> findAll();

    List<Course> findByTeacherId(int teacherId);

    List<Course> findByStudentId(int studentId);

    List<Course> search(String query, int page, int pageSize);

    int count(String query);
}
