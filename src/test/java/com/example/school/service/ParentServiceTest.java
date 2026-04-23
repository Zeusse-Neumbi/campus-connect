package com.example.school.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.example.school.dao.*;
import com.example.school.model.*;
import java.util.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class ParentServiceTest {

    @Mock private ParentDao parentDao;
    @Mock private StudentParentDao studentParentDao;
    @Mock private StudentDao studentDao;
    @Mock private UserDao userDao;
    @Mock private CourseDao courseDao;
    @Mock private EnrollmentDao enrollmentDao;
    @Mock private ExamDao examDao;
    @Mock private ExamResultDao examResultDao;
    @Mock private AttendanceDao attendanceDao;
    @Mock private CourseSessionDao courseSessionDao;

    @InjectMocks
    private ParentService parentService;

    @Test
    void getParentByUserId_ShouldReturnParent_WhenFound() {
        Parent parent = new Parent(1, 10, "123 Main St", "Engineer");
        when(parentDao.findByUserId(10)).thenReturn(Optional.of(parent));

        Optional<Parent> result = parentService.getParentByUserId(10);
        assertTrue(result.isPresent());
        assertEquals(1, result.get().getId());
    }

    @Test
    void getLinkedStudents_ShouldReturnStudentAndUserData() {
        int parentId = 1;
        List<Map<String, Object>> mockLinks = new ArrayList<>();
        Map<String, Object> link1 = new HashMap<>();
        link1.put("studentId", 100);
        link1.put("relationship", "Father");
        mockLinks.add(link1);

        Student mockStudent = new Student(100, 200, "STU001", "2000-01-01", "MALE", "2020-01-01");
        User mockUser = new User(200, "stu@test.com", "pass", 3, "John", "Doe");

        when(studentParentDao.findStudentsByParentId(parentId)).thenReturn(mockLinks);
        when(studentDao.findById(100)).thenReturn(Optional.of(mockStudent));
        when(userDao.findById(200)).thenReturn(Optional.of(mockUser));

        List<Map<String, Object>> result = parentService.getLinkedStudents(parentId);

        assertEquals(1, result.size());
        assertEquals(mockStudent, result.get(0).get("student"));
        assertEquals(mockUser, result.get(0).get("user"));
        assertEquals("Father", result.get(0).get("relationship"));
    }

    @Test
    void isLinkedToStudent_ShouldReturnTrue_WhenLinkExists() {
        int parentId = 1;
        int studentId = 100;

        List<Map<String, Object>> mockLinks = new ArrayList<>();
        Map<String, Object> link1 = new HashMap<>();
        link1.put("studentId", studentId);
        mockLinks.add(link1);

        when(studentParentDao.findStudentsByParentId(parentId)).thenReturn(mockLinks);

        assertTrue(parentService.isLinkedToStudent(parentId, studentId));
    }

    @Test
    void isLinkedToStudent_ShouldReturnFalse_WhenLinkDoesNotExist() {
        int parentId = 1;
        int searchedStudentId = 999;

        List<Map<String, Object>> mockLinks = new ArrayList<>();
        Map<String, Object> link1 = new HashMap<>();
        link1.put("studentId", 100);
        mockLinks.add(link1);

        when(studentParentDao.findStudentsByParentId(parentId)).thenReturn(mockLinks);

        assertFalse(parentService.isLinkedToStudent(parentId, searchedStudentId));
    }

    @Test
    void getStudentCourses_ShouldReturnCoursesLinkedToEnrollments() {
        int studentId = 100;
        Enrollment enrollment = new Enrollment(1, studentId, 10, "2026-01-01");
        Course course = new Course(10, "CS101", "Intro CS", "Desc", 3, 5);

        when(enrollmentDao.findByStudentId(studentId)).thenReturn(Arrays.asList(enrollment));
        when(courseDao.findById(10)).thenReturn(Optional.of(course));

        List<Course> courses = parentService.getStudentCourses(studentId);

        assertEquals(1, courses.size());
        assertEquals("CS101", courses.get(0).getCourseCode());
    }

    @Test
    void getStudentDashboardStats_ShouldCalculateCorrectStats() {
        int studentId = 100;
        
        // Mock enrolments (count = 2)
        when(enrollmentDao.findByStudentId(studentId)).thenReturn(Arrays.asList(
            new Enrollment(1, studentId, 10, ""),
            new Enrollment(2, studentId, 11, "")
        ));

        // Mock grades (avg = 15.0)
        when(examResultDao.findByStudentId(studentId)).thenReturn(Arrays.asList(
            new ExamResult(1, 1, studentId, 14, "", "Good"),
            new ExamResult(2, 2, studentId, 16, "", "Excellent")
        ));

        // Mock attendance (rate = 85.0)
        when(attendanceDao.getAttendanceRate(studentId)).thenReturn(85.0);

        Map<String, Object> stats = parentService.getStudentDashboardStats(studentId);

        assertEquals(2, stats.get("courseCount"));
        assertEquals("15.0", stats.get("gpa")); // (14+16)/2 = 15.0
        assertEquals("85", stats.get("attendanceRate"));
    }
}
