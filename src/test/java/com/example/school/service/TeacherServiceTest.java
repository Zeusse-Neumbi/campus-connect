package com.example.school.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
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
public class TeacherServiceTest {

    @Mock
    private TeacherDao teacherDao;
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
    private StudentDao studentDao;
    @Mock
    private UserDao userDao;
    @Mock
    private CourseSessionDao courseSessionDao;
    @Mock
    private ClassroomDao classroomDao;

    @InjectMocks
    private TeacherService teacherService;

    @Test
    void getTeacherCourses_ShouldReturnCourses() {
        int teacherId = 1;
        Course c1 = new Course(101, "MATH101", "Math", "desc", 3, teacherId);
        when(courseDao.findByTeacherId(teacherId)).thenReturn(Collections.singletonList(c1));

        List<Course> courses = teacherService.getTeacherCourses(teacherId);

        assertEquals(1, courses.size());
        assertEquals("Math", courses.get(0).getCourseName());
    }

    @Test
    void getStudentCountForTeacher_ShouldReturnTotalStudents() {
        int teacherId = 1;
        Course c1 = new Course(101, "MATH101", "Math", "desc", 3, teacherId);
        Course c2 = new Course(102, "SCI101", "Science", "desc", 3, teacherId);
        when(courseDao.findByTeacherId(teacherId)).thenReturn(Arrays.asList(c1, c2));

        Enrollment e1 = new Enrollment(1, 1, 101, "2023-01-01");
        Enrollment e2 = new Enrollment(2, 2, 101, "2023-01-01");
        Enrollment e3 = new Enrollment(3, 3, 102, "2023-01-01");

        when(enrollmentDao.findByCourseId(101)).thenReturn(Arrays.asList(e1, e2));
        when(enrollmentDao.findByCourseId(102)).thenReturn(Collections.singletonList(e3));

        int count = teacherService.getStudentCountForTeacher(teacherId);

        assertEquals(3, count);
    }

    @Test
    void updateExamResult_ShouldUpdateExisting_WhenResultExists() {
        int examId = 1;
        int studentId = 1;
        int score = 18;
        ExamResult existing = new ExamResult(1, examId, studentId, 15, "2023-01-01", null);

        when(examResultDao.findByExamIdAndStudentId(examId, studentId)).thenReturn(Optional.of(existing));

        teacherService.updateExamResult(examId, studentId, score);

        verify(examResultDao).update(any(ExamResult.class));
        verify(examResultDao, never()).save(any(ExamResult.class));
    }

    @Test
    void updateExamResult_ShouldCreateNew_WhenNoResultExists() {
        int examId = 1;
        int studentId = 1;
        int score = 18;

        when(examResultDao.findByExamIdAndStudentId(examId, studentId)).thenReturn(Optional.empty());

        teacherService.updateExamResult(examId, studentId, score);

        verify(examResultDao).save(any(ExamResult.class));
        verify(examResultDao, never()).update(any(ExamResult.class));
    }

    @Test
    void updateAttendance_ShouldUpdateExisting_WhenRecordExists() {
        int sessionId = 1;
        int studentId = 1;
        String status = "PRESENT";
        Attendance existing = new Attendance(1, sessionId, studentId, "ABSENT", "2023-01-15");

        when(attendanceDao.findBySessionIdAndStudentId(sessionId, studentId)).thenReturn(Optional.of(existing));

        teacherService.updateAttendance(sessionId, studentId, status);

        verify(attendanceDao).update(any(Attendance.class));
        verify(attendanceDao, never()).save(any(Attendance.class));
    }

    @Test
    void updateAttendance_ShouldCreateNew_WhenNoRecordExists() {
        int sessionId = 1;
        int studentId = 1;
        String status = "PRESENT";

        when(attendanceDao.findBySessionIdAndStudentId(sessionId, studentId)).thenReturn(Optional.empty());

        teacherService.updateAttendance(sessionId, studentId, status);

        verify(attendanceDao).save(any(Attendance.class));
        verify(attendanceDao, never()).update(any(Attendance.class));
    }
}
