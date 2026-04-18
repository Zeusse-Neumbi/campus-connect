package com.example.school.dao;

import com.example.school.model.Classroom;
import java.util.List;
import java.util.Optional;

public interface ClassroomDao {
    int save(Classroom classroom);

    Optional<Classroom> findById(int id);

    List<Classroom> findAll();

    void update(Classroom classroom);

    void delete(int id);

    List<Classroom> search(String query, int page, int pageSize);
    int count(String query);

    List<Classroom> searchWithFilters(String query, List<String> buildings, Integer minCapacity, int page, int pageSize);
    int countWithFilters(String query, List<String> buildings, Integer minCapacity);
}
