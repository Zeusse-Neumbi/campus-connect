package com.example.school.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class UserTest {

    @Test
    void user_ShouldBeCreatedCorrectly_WhenUsingConstructor() {
        User user = new User(1, "test@test.com", "pass", 1, "John", "Doe");
        
        assertEquals(1, user.getId());
        assertEquals("test@test.com", user.getEmail());
        assertEquals("pass", user.getPassword());
        assertEquals(1, user.getRoleId());
        assertEquals("John", user.getFirstName());
        assertEquals("Doe", user.getLastName());
    }

    @Test
    void user_SettersAndGetters_ShouldWorkCorrectly() {
        User user = new User(0, "", "", 0, "", "");
        
        user.setId(2);
        user.setEmail("new@test.com");
        user.setPassword("newpass");
        user.setRoleId(2);
        user.setFirstName("Jane");
        user.setLastName("Smith");
        user.setPhone("123456789");
        user.setCreatedAt("2026-01-01");
        user.setUpdatedAt("2026-01-02");

        assertEquals(2, user.getId());
        assertEquals("new@test.com", user.getEmail());
        assertEquals("newpass", user.getPassword());
        assertEquals(2, user.getRoleId());
        assertEquals("Jane", user.getFirstName());
        assertEquals("Smith", user.getLastName());
        assertEquals("123456789", user.getPhone());
        assertEquals("2026-01-01", user.getCreatedAt());
        assertEquals("2026-01-02", user.getUpdatedAt());
    }

    @Test
    void getRole_ShouldReturnCorrectEnum_WhenRoleIdExists() {
        User user = new User(1, "test@test.com", "pass", Role.ADMIN.getId(), "John", "Doe");
        assertEquals(Role.ADMIN, user.getRole());
    }

    @Test
    void setRole_ShouldUpdateRoleId_WhenRoleIsNotNull() {
        User user = new User(1, "", "", 1, "", "");
        
        user.setRole(Role.TEACHER);
        assertEquals(Role.TEACHER.getId(), user.getRoleId());
        
        user.setRole(null);
        assertEquals(Role.TEACHER.getId(), user.getRoleId()); // roleId shouldn't change
    }
}
