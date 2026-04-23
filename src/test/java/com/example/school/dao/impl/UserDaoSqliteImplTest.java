package com.example.school.dao.impl;

import static org.junit.jupiter.api.Assertions.*;

import com.example.school.model.User;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class UserDaoSqliteImplTest {

    private UserDaoSqliteImpl userDao;

    @BeforeEach
    void setUp() throws Exception {
        userDao = new UserDaoSqliteImpl();
    }

    @Test
    void saveAndFindById_ShouldWorkCorrectly() {
        String randomEmail = "test" + UUID.randomUUID() + "@test.com";
        User user = new User(0, randomEmail, "password", 1, "Admin", "Test");
        user.setPhone("12345");

        int id = userDao.save(user);
        assertTrue(id > 0, "Saved User ID should be greater than 0");

        Optional<User> found = userDao.findById(id);
        assertTrue(found.isPresent());
        assertEquals(randomEmail, found.get().getEmail());
        assertEquals("Admin", found.get().getFirstName());
        assertEquals("12345", found.get().getPhone());
    }

    @Test
    void findByEmail_ShouldReturnUser_WhenExists() {
        String randomEmail = "email" + UUID.randomUUID() + "@test.com";
        User user = new User(0, randomEmail, "pass", 2, "Teacher", "Test");
        
        userDao.save(user);

        Optional<User> found = userDao.findByEmail(randomEmail);
        assertTrue(found.isPresent());
        assertEquals("Teacher", found.get().getFirstName());
    }

    @Test
    void update_ShouldChangeUserData() {
        String email = "update" + UUID.randomUUID() + "@test.com";
        User user = new User(0, email, "pass", 3, "Student", "Old");
        
        int id = userDao.save(user);
        user.setId(id);
        
        // Update data
        user.setLastName("New");
        user.setPhone("999");
        userDao.update(user);

        Optional<User> found = userDao.findById(id);
        assertTrue(found.isPresent());
        assertEquals("New", found.get().getLastName());
        assertEquals("999", found.get().getPhone());
    }

    @Test
    void delete_ById_ShouldRemoveUser() {
        String email = "delete_id" + UUID.randomUUID() + "@test.com";
        User user = new User(0, email, "pass", 4, "Parent", "M");
        
        int id = userDao.save(user);
        Optional<User> found = userDao.findById(id);
        assertTrue(found.isPresent());

        userDao.delete(id);
        
        Optional<User> deleted = userDao.findById(id);
        assertFalse(deleted.isPresent());
    }

    @Test
    void search_ShouldReturnMatchingUsers() {
        String email1 = "search1" + UUID.randomUUID() + "@test.com";
        String email2 = "search2" + UUID.randomUUID() + "@test.com";
        userDao.save(new User(0, email1, "p", 1, "Alpha", "Omega"));
        userDao.save(new User(0, email2, "p", 1, "Beta", "Gamma"));

        java.util.List<User> results = userDao.search("Alpha", 1, 10);
        assertEquals(1, results.size());
        assertEquals("Alpha", results.get(0).getFirstName());
        
        results = userDao.search(email2, 1, 10);
        assertEquals(1, results.size());
        assertEquals("Beta", results.get(0).getFirstName());
    }
}
