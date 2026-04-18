package com.example.school.dao;

import com.example.school.model.CourseGroup;
import java.util.List;
import java.util.Optional;

public interface CourseGroupDao {
    int save(CourseGroup group);

    Optional<CourseGroup> findById(int id);

    List<CourseGroup> findAll();

    List<CourseGroup> findByCourseId(int courseId);

    void update(CourseGroup group);

    void delete(int id);

    List<CourseGroup> search(String query, int page, int pageSize);

    int count(String query);
}
