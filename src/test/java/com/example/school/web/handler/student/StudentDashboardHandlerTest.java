package com.example.school.web.handler.student;

import static org.mockito.Mockito.*;

import com.example.school.model.Student;
import com.example.school.service.ServiceFactory;
import com.example.school.service.StudentService;
import com.example.school.service.UserService;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Collections;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class StudentDashboardHandlerTest {

    @Mock private HttpServletRequest request;
    @Mock private HttpServletResponse response;
    @Mock private RequestDispatcher dispatcher;
    @Mock private StudentService studentService;
    @Mock private UserService userService;

    private MockedStatic<ServiceFactory> mockedServiceFactory;
    private StudentDashboardHandler handler;

    @BeforeEach
    void setUp() {
        mockedServiceFactory = mockStatic(ServiceFactory.class);
        mockedServiceFactory.when(ServiceFactory::getStudentService).thenReturn(studentService);
        mockedServiceFactory.when(ServiceFactory::getUserService).thenReturn(userService);
        handler = new StudentDashboardHandler();
    }

    @AfterEach
    void tearDown() {
        mockedServiceFactory.close();
    }

    @Test
    void handleGet_ShouldShowDashboard() throws Exception {
        Student student = new Student(1, 10, "S123", "2000-01-01", "M", "2020");
        when(request.getAttribute("student")).thenReturn(student);
        when(request.getPathInfo()).thenReturn("/dashboard");

        when(studentService.getCourseCount(1)).thenReturn(5);
        when(studentService.calculateGPA(1)).thenReturn(14.5);
        when(studentService.calculateOverallAverageOver20(1)).thenReturn(14.5);
        when(studentService.getAttendanceRate(1)).thenReturn(90.0);
        when(studentService.getTodaySessions(1)).thenReturn(Collections.emptyList());
        when(studentService.getWeekSessions(eq(1), any())).thenReturn(Collections.emptyList());
        when(studentService.getEnrolledCourses(1)).thenReturn(Collections.emptyList());
        
        when(request.getRequestDispatcher("/WEB-INF/views/student/dashboard.jsp")).thenReturn(dispatcher);

        handler.handleGet(request, response);

        verify(request).setAttribute("courseCount", 5);
        verify(request).setAttribute("gpa", "14.50");
        verify(request).setAttribute("attendanceRate", "90");
        verify(dispatcher).forward(request, response);
    }

    @Test
    void handleGet_ShouldShowProfile() throws Exception {
        when(request.getPathInfo()).thenReturn("/profile");
        when(request.getRequestDispatcher("/WEB-INF/views/student/profile.jsp")).thenReturn(dispatcher);

        handler.handleGet(request, response);

        verify(dispatcher).forward(request, response);
    }
}
