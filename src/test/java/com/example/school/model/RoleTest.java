package com.example.school.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class RoleTest {

    @Test
    void role_ShouldHaveCorrectIds() {
        assertEquals(1, Role.ADMIN.getId());
        assertEquals(2, Role.TEACHER.getId());
        assertEquals(3, Role.STUDENT.getId());
        assertEquals(4, Role.PARENT.getId());
    }

    @Test
    void fromId_ShouldReturnCorrectRole_WhenIdIsValid() {
        assertEquals(Role.ADMIN, Role.fromId(1));
        assertEquals(Role.TEACHER, Role.fromId(2));
        assertEquals(Role.STUDENT, Role.fromId(3));
        assertEquals(Role.PARENT, Role.fromId(4));
    }

    @Test
    void fromId_ShouldReturnNull_WhenIdIsInvalid() {
        assertNull(Role.fromId(0));
        assertNull(Role.fromId(5));
        assertNull(Role.fromId(-1));
    }
}
