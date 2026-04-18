package com.example.school.dao;

import com.example.school.model.Parent;
import java.util.List;
import java.util.Optional;

public interface ParentDao {
    int save(Parent parent);

    Optional<Parent> findById(int id);

    Optional<Parent> findByUserId(int userId);

    List<Parent> findAll();

    void update(Parent parent);

    void delete(int id);

    void deleteByUserId(int userId);

    int count();

    int count(String query);

    List<Parent> search(String query, int page, int pageSize);
}
