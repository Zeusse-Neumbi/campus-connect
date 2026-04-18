package com.example.school.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.school.dao.*;
import com.example.school.model.*;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class StudentServiceTest {

    @Mock
    private StudentDao studentDao;
    @Mock
    private CourseDao courseDao;
    @Mock
    private EnrollmentDao enrollmentDao;
    @Mock
    private ExamDao examDao;
    @Mock
    private ExamResultDao examResultDao;
    @Mock
    private AttendanceDao attendanceDao;
    @Mock
    private TeacherDao teacherDao;
    @Mock
    private UserDao userDao;
    @Mock
    private CourseSessionDao courseSessionDao;

    @InjectMocks
    private StudentService studentService;

    @Test
    void calculateGPA_ShouldReturnCorrectAverage() {
        int studentId = 1;

        ExamResult r1 = new ExamResult(1, 1, studentId, 15, "2023-01-10", null);
        ExamResult r2 = new ExamResult(2, 2, studentId, 10, "2023-01-12", null);

        when(examResultDao.findByStudentId(studentId)).thenReturn(Arrays.asList(r1, r2));

        double gpa = studentService.calculateGPA(studentId);

        assertEquals(12.5, gpa, 0.01);
    }

    @Test
    void calculateGPA_ShouldReturnZeroWhenNoResults() {
        int studentId = 1;
        when(examResultDao.findByStudentId(studentId)).thenReturn(Collections.emptyList());

        double gpa = studentService.calculateGPA(studentId);

        assertEquals(0.0, gpa, 0.01);
    }

    @Test
    void getTeacherName_ShouldReturnName_WhenTeacherExists() {
        int courseId = 101;
        int teacherId = 5;
        int userId = 10;

        Course course = new Course(courseId, "MATH101", "Mathematics", "Intro to Math", 3, teacherId);
        Teacher teacher = new Teacher(teacherId, userId, "EMP001", "Math Dept", "2020-01-01");
        User user = new User(userId, "john@example.com", "pass", 2, "John", "Doe");

        when(courseDao.findById(courseId)).thenReturn(Optional.of(course));
        when(teacherDao.findById(teacherId)).thenReturn(Optional.of(teacher));
        when(userDao.findById(userId)).thenReturn(Optional.of(user));

        String teacherName = studentService.getTeacherName(courseId);

        assertEquals("John Doe", teacherName);
    }

    @Test
    void getTeacherName_ShouldReturnUnknown_WhenCourseNotFound() {
        when(courseDao.findById(anyInt())).thenReturn(Optional.empty());

        String name = studentService.getTeacherName(999);

        assertEquals("Unknown", name);
    }

    @Test
    void getAvailableCourses_ShouldExcludeEnrolledCourses() {
        int studentId = 1;
        Course c1 = new Course(101, "MATH101", "Math", "desc", 3, 5);
        Course c2 = new Course(102, "SCI101", "Science", "desc", 3, 6);
        Course c3 = new Course(103, "HIST101", "History", "desc", 3, 7);

        when(courseDao.findAll()).thenReturn(Arrays.asList(c1, c2, c3));

        Enrollment e1 = new Enrollment(1, studentId, 101, "2023-01-01");
        when(enrollmentDao.findByStudentId(studentId)).thenReturn(Collections.singletonList(e1));

        List<Course> available = studentService.getAvailableCourses(studentId);

        assertEquals(2, available.size());
        assertTrue(available.contains(c2));
        assertTrue(available.contains(c3));
        assertFalse(available.contains(c1));
    }

    @Test
    void enroll_ShouldCallDao_WhenCourseIsAvailable() {
        int studentId = 1;
        int courseId = 101;

        studentService.enroll(studentId, courseId);

        verify(enrollmentDao).save(any(Enrollment.class));
    }

    @Test
    void unenroll_ShouldCallDao_WhenEnrolled() {
        int studentId = 1;
        int courseId = 101;

        studentService.unenroll(studentId, courseId);

        verify(enrollmentDao).delete(studentId, courseId);
    }

    @Test
    void getExamResults_ShouldReturnResults() {
        int studentId = 1;
        ExamResult r1 = new ExamResult(1, 1, studentId, 15, "2023-01-10", null);
        ExamResult r2 = new ExamResult(2, 2, studentId, 10, "2023-01-12", null);

        when(examResultDao.findByStudentId(studentId)).thenReturn(Arrays.asList(r1, r2));

        List<ExamResult> results = studentService.getExamResults(studentId);

        assertEquals(2, results.size());
        assertEquals(15, results.get(0).getScore());
    }
}
