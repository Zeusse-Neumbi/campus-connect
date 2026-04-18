package com.example.school.dao;

import com.example.school.model.ExamResult;
import java.util.List;
import java.util.Optional;

public interface ExamResultDao {
    void save(ExamResult result);

    void update(ExamResult result);

    List<ExamResult> findByExamId(int examId);

    List<ExamResult> findByStudentId(int studentId);

    Optional<ExamResult> findByExamIdAndStudentId(int examId, int studentId);
}
