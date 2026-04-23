package com.example.school.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class CourseTest {

    @Test
    void course_ShouldBeCreatedCorrectly() {
        Course course = new Course(1, "CS101", "Intro to CS", "A basic CS course", 3, 10);
        
        assertEquals(1, course.getId());
        assertEquals("CS101", course.getCourseCode());
        assertEquals("Intro to CS", course.getCourseName());
        assertEquals("A basic CS course", course.getDescription());
        assertEquals(3, course.getCredits());
        assertEquals(10, course.getTeacherId());
    }

    @Test
    void course_Setters_ShouldUpdateValues() {
        Course course = new Course(0, "", "", "", 0, 0);

        course.setId(2);
        course.setCourseCode("MATH200");
        course.setCourseName("Calculus");
        course.setDescription("Advanced Calculus");
        course.setCredits(4);
        course.setTeacherId(5);
        course.setCreatedAt("2026-05-01");
        course.setUpdatedAt("2026-05-02");

        assertEquals(2, course.getId());
        assertEquals("MATH200", course.getCourseCode());
        assertEquals("Calculus", course.getCourseName());
        assertEquals("Advanced Calculus", course.getDescription());
        assertEquals(4, course.getCredits());
        assertEquals(5, course.getTeacherId());
        assertEquals("2026-05-01", course.getCreatedAt());
        assertEquals("2026-05-02", course.getUpdatedAt());
    }
}
