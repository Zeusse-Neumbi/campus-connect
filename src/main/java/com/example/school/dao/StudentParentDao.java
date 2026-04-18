package com.example.school.dao;

import java.util.List;
import java.util.Map;

public interface StudentParentDao {
    void link(int studentId, int parentId, String relationship);

    void unlink(int studentId, int parentId);

    /** Returns list of maps with keys: studentId, relationship */
    List<Map<String, Object>> findStudentsByParentId(int parentId);

    /** Returns list of maps with keys: parentId, relationship */
    List<Map<String, Object>> findParentsByStudentId(int studentId);

    List<Map<String, Object>> findAll();

    List<Map<String, Object>> search(String query, int page, int pageSize);

    int count(String query);
}
