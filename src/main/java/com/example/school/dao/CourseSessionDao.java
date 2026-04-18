package com.example.school.dao;

import com.example.school.model.CourseSession;
import java.util.List;
import java.util.Optional;

public interface CourseSessionDao {
    int save(CourseSession session);

    Optional<CourseSession> findById(int id);

    List<CourseSession> findAll();

    List<CourseSession> findByCourseId(int courseId);

    List<CourseSession> findByDate(String date);

    List<CourseSession> findByDateRange(String startDate, String endDate);

    List<CourseSession> findByCourseIdAndDate(int courseId, String date);

    void update(CourseSession session);

    void delete(int id);

    List<CourseSession> search(String query, int page, int pageSize);

    int count(String query);
}
