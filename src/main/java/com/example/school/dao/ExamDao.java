package com.example.school.dao;

import com.example.school.model.Exam;
import java.util.List;
import java.util.Optional;

public interface ExamDao {
    void save(Exam exam);

    void update(Exam exam);

    void delete(int id);

    Optional<Exam> findById(int id);

    List<Exam> findByCourseId(int courseId);
}
