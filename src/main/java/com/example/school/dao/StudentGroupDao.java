package com.example.school.dao;

import com.example.school.model.StudentGroup;
import java.util.List;

public interface StudentGroupDao {
    int save(StudentGroup studentGroup);
    void delete(int studentId, int courseGroupId);
    List<StudentGroup> findByCourseGroupId(int courseGroupId);
    List<StudentGroup> findByStudentId(int studentId);
}
